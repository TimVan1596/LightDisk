/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.gossip.model;

public class ShutdownMessage extends Message {

  public static final String PER_NODE_KEY = "gossipcore.shutdowmessage";
  private long shutdownAtNanos;
  private String nodeId;
  
  public ShutdownMessage(){
    
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public long getShutdownAtNanos() {
    return shutdownAtNanos;
  }

  public void setShutdownAtNanos(long shutdownAtNanos) {
    this.shutdownAtNanos = shutdownAtNanos;
  }

  @Override
  public String toString() {
    return "ShutdownMessage [shutdownAtNanos=" + shutdownAtNanos + ", nodeId=" + nodeId + "]";
  }
  
}
