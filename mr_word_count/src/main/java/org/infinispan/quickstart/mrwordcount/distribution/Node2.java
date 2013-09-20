/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other
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
package org.infinispan.quickstart.mrwordcount.distribution;

import org.infinispan.Cache;
import org.infinispan.quickstart.mrwordcount.util.LoggingListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Iterator;

import org.infinispan.distexec.mapreduce.MapReduceTask;
import org.infinispan.distexec.mapreduce.Reducer;
import org.infinispan.distexec.mapreduce.Mapper;
import org.infinispan.distexec.mapreduce.Collector;

public class Node2 extends AbstractNode {
	
   static OutputStream fos = null;

   public static void main(String[] args) throws Exception {
      new Node2().run();
   }

   public void run() {	         
	         
      Cache<String, String> cache = getCacheManager().getCache("Demo");

      waitForClusterToForm();

      // Add a listener so that we can see the puts to this node
      cache.addListener(new LoggingListener());

      populate2(cache);
      
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


   
   /**
    * Reads in the completed works of Shakspeare and put each line in the cache
    *
    * @param c - the cache
    */
   private void populate2(Cache c) {
           
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
   
   @Override
   protected int getNodeId() {
      return 2;
   }

}

