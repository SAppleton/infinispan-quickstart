/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.quickstart.mrwordcount;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;

import org.infinispan.distexec.mapreduce.MapReduceTask;
import org.infinispan.distexec.mapreduce.Reducer;
import org.infinispan.distexec.mapreduce.Mapper;
import org.infinispan.distexec.mapreduce.Collector;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.quickstart.mrwordcount.util.LoggingListener;
import org.infinispan.commons.logging.BasicLogFactory;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Node {

   private static final BasicLogger log = Logger.getLogger(Node.class);

   private final boolean useXmlConfig;
   private volatile boolean isController = false;
   private final String nodeName;
   private volatile boolean stop = false;
   static OutputStream fos = null;

   public Node(boolean useXmlConfig, boolean isControl, String nodeName) {
      this.useXmlConfig = useXmlConfig;
      this.isController = isControl;
      this.nodeName = nodeName;
   }

   public static void main(String[] args) throws Exception {
      boolean useXmlConfig = false;
      boolean isControl = false;
      String nodeName = null;

      for (String arg : args) {
         if ("-x".equals(arg)) {
            useXmlConfig = true;
         } else if ("-p".equals(arg)) {
            useXmlConfig = false;
         } else if ("-c".equals(arg)) {
            isControl = true;  //Special case a controller node to populate the cache
         } else {
            nodeName = arg;
         }
      }
      new Node(useXmlConfig, isControl, nodeName).run();
   }
   

   public void run() throws IOException, InterruptedException {
      EmbeddedCacheManager cacheManager = createCacheManager();
      final Cache<String, String> cache = cacheManager.getCache("dist");
      System.out.printf("Cache %s started on %s, cache members are now %s\n", "dist", cacheManager.getAddress(),
            cache.getAdvancedCache().getRpcManager().getMembers());  

      // Add a listener so that we can see the puts to this node
      cache.addListener(new LoggingListener());

      printCacheContents(cache);

// if the node is a controller node then that does all the co-ordination
      if (isController){
         Thread putThread = new Thread() {
            @Override
            public void run() {

               populate(cache);
      
               try{
	         fos = new FileOutputStream("results.txt");
	       }
	       catch(Exception e){
	    	  System.out.println(e.getMessage());
               }
      
               countWords(cache);
      
               try{
                  fos.close();
               }
	       catch(Exception e){
    	          System.out.println(e.getMessage());
               } 
           }
         };
      putThread.start();

      System.out.println("Press Enter to print the cache contents, Ctrl+D/Ctrl+Z to stop.");
      while (System.in.read() > 0) {
         printCacheContents(cache);
      }

      stop = true;
      putThread.join();
      cacheManager.stop();
      System.exit(0);
}
   }

   /**
    * {@link org.infinispan.Cache#entrySet()}
    * @param cache
    */
   private void printCacheContents(Cache<String, String> cache) {
      System.out.printf("Cache contents on node %s\n", cache.getAdvancedCache().getRpcManager().getAddress());

      ArrayList<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(cache.entrySet());
      Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {
         @Override
         public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
            return o1.getKey().compareTo(o2.getKey());
         }
      });
      for (Map.Entry<String, String> e : entries) {
         System.out.printf("\t%s = %s\n", e.getKey(), e.getValue());
      }
      System.out.println();
   }

   private EmbeddedCacheManager createCacheManager() throws IOException {
      if (useXmlConfig) {
         return createCacheManagerFromXml();
      } else {
         return createCacheManagerProgrammatically();
      }
   }

   private EmbeddedCacheManager createCacheManagerProgrammatically() {
      System.out.println("Starting a cache manager with a programmatic configuration");
      DefaultCacheManager cacheManager = new DefaultCacheManager(
            GlobalConfigurationBuilder.defaultClusteredBuilder()
                  .transport().nodeName(nodeName).addProperty("configurationFile", "jgroups.xml")
                  .build(),
            new ConfigurationBuilder()
                  .clustering()
                  .cacheMode(CacheMode.REPL_SYNC)
                  .build()
      );
      // only dist caches allowed
      cacheManager.defineConfiguration("dist", new ConfigurationBuilder()
            .clustering()
            .cacheMode(CacheMode.DIST_SYNC)
            .hash().numOwners(2)
            .build()
      );
      return cacheManager;
   }

   private EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
      System.out.println("Starting a cache manager with an XML configuration");
      System.setProperty("nodeName", nodeName);
      return new DefaultCacheManager("infinispan.xml");
   }

   
   /**
    * Reads in the complete works of Shakespeare and puts each line in the cache
    *
    * @param c - the cache
    */
   private void populate(Cache c) {
           
      InputStream    fis;
      BufferedReader br = null;
      String         line;      
      int i = 1;
      
      try{
         fis = new FileInputStream("shaks12.txt");
         br = new BufferedReader(new InputStreamReader(fis));
         while ((line = br.readLine()) != null) {
            c.putAsync(""+i, line);
            i++;
         }
         br.close();
         br = null;
         fis = null;
      }
      catch(Exception e){
    	  System.out.println(e.getMessage());
      }
   }
   
   private void countWords(Cache c)
   {
	      MapReduceTask<String, String, String, Integer> t =
	    	         new MapReduceTask<String, String, String, Integer>(c);
	    	      t.mappedWith(new WordCountMapper())
	    	         .reducedWith(new WordCountReducer());
	    	      Map<String, Integer> wordCountMap = t.execute();
   }

   
   private static void writeToFile(String b) {

	      try{
	         fos.write(b.getBytes());
	      }
	      catch(Exception e){
	    	  System.out.println(e.getMessage());
	      }
   }
   

 
   static class WordCountMapper implements Mapper<String,String,String,Integer> {
      /** The serialVersionUID */
      private static final long serialVersionUID = -5943370243108735560L;
 
      @Override
      public void map(String key, String value, Collector<String, Integer> c) {
         StringTokenizer tokens = new StringTokenizer(value);
         while (tokens.hasMoreElements()) {
            String s = (String) tokens.nextElement();
            c.emit(s, 1);
         }
      }
   }
   
   static class WordCountReducer implements Reducer<String, Integer> {
	      /** The serialVersionUID */
	      private static final long serialVersionUID = 1901016598354633256L;
	 
	      @Override
	      public Integer reduce(String key, Iterator<Integer> iter) {
	         int sum = 0;
	         while (iter.hasNext()) {
	            Integer i = (Integer) iter.next();
	            sum += i;
	         }
	         System.out.println(key + " count: "+ sum);
                 writeToFile(key + " count: "+ sum + System.getProperty("line.separator"));
	         return sum;
	      }
	   }   
   
}
