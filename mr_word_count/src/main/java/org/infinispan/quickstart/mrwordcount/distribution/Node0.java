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
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Iterator;
import org.infinispan.distexec.mapreduce.MapReduceTask;
import org.infinispan.distexec.mapreduce.Reducer;
import org.infinispan.distexec.mapreduce.Mapper;
import org.infinispan.distexec.mapreduce.Collector;

public class Node0 extends AbstractNode {

   public static void main(String[] args) throws Exception {
      new Node0().run();
   }
   
   public void run() {
      Cache<String, String> cache = getCacheManager().getCache("Demo");

      // Add a listener so that we can see the puts to this node
      cache.addListener(new LoggingListener());

      waitForClusterToForm();
      //populate(cache);
   }

/*

   /**
    * In this example replace c1 and c2 with
    * real Cache references
    *
    * @param args
    */
   private void populate(Cache c) {

      System.out.println("Populate");
 
      c.put("1", "Hello world here I am");
      c.put("2", "Infinispan rules the world");
      c.put("3", "JUDCon is in Boston");
      c.put("4", "JBoss World is in Boston as well");
      c.put("12","JBoss Application Server");
      c.put("15", "Hello world");
      c.put("14", "Infinispan community");
      c.put("15", "Hello world");
 
      c.put("111", "Infinispan open source");
      c.put("112", "Boston is close to Toronto");
      c.put("113", "Toronto is a capital of Ontario");
      c.put("114", "JUDCon is cool");
      c.put("211", "JBoss World is awesome");
      c.put("212", "JBoss rules");
      c.put("213", "JBoss division of RedHat ");
      c.put("214", "RedHat community");
 
      MapReduceTask<String, String, String, Integer> t =
         new MapReduceTask<String, String, String, Integer>(c);
      t.mappedWith(new WordCountMapper())
         .reducedWith(new WordCountReducer());
      Map<String, Integer> wordCountMap = t.execute();
   }
 
   class WordCountMapper implements Mapper<String,String,String,Integer> {
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
 
   class WordCountReducer implements Reducer<String, Integer> {
      /** The serialVersionUID */
      private static final long serialVersionUID = 1901016598354633256L;
 
      @Override
      public Integer reduce(String key, Iterator<Integer> iter) {
         int sum = 0;
         while (iter.hasNext()) {
            Integer i = (Integer) iter.next();
            sum += i;
         }
         return sum;
      }
   }


   
   @Override
   protected int getNodeId() {
      return 0;
   }

}
