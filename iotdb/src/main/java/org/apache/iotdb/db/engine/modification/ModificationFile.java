/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.engine.modification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.iotdb.db.engine.modification.io.LocalTextModificationAccessor;
import org.apache.iotdb.db.engine.modification.io.ModificationReader;
import org.apache.iotdb.db.engine.modification.io.ModificationWriter;

/**
 * ModificationFile stores the Modifications of a TsFile or unseq file in another file in the same
 * directory. Methods in this class are highly synchronized for concurrency safety.
 */
public class ModificationFile {

  public static final String FILE_SUFFIX = ".mods";

  private List<Modification> modifications;
  private ModificationWriter writer;
  private ModificationReader reader;
  private String filePath;

  /**
   * Construct a ModificationFile using a file as its storage.
   *
   * @param filePath the path of the storage file.
   */
  public ModificationFile(String filePath) {
    LocalTextModificationAccessor accessor = new LocalTextModificationAccessor(filePath);
    this.writer = accessor;
    this.reader = accessor;
    this.filePath = filePath;
  }

  private void init() throws IOException {
    synchronized (this) {
      modifications = (List<Modification>) reader.read();
    }
  }

  private void checkInit() throws IOException {
    if (modifications == null) {
      init();
    }
  }

  /**
   * Release resources such as streams and caches.
   */
  public void close() throws IOException {
    synchronized (this) {
      writer.close();
      modifications = null;
    }
  }

  public void abort() throws IOException {
    synchronized (this) {
      if (modifications.size() > 0) {
        writer.abort();
        modifications.remove(modifications.size() - 1);
      }
    }
  }

  /**
   * Write a modification in this file. The modification will first be written to the persistent
   * store then the memory cache.
   *
   * @param mod the modification to be written.
   * @throws IOException if IOException is thrown when writing the modification to the store.
   */
  public void write(Modification mod) throws IOException {
    synchronized (this) {
      checkInit();
      writer.write(mod);
      modifications.add(mod);
    }
  }

  /**
   * Get all modifications stored in this file.
   *
   * @return an ArrayList of modifications.
   */
  public Collection<Modification> getModifications() throws IOException {
    synchronized (this) {
      checkInit();
      return new ArrayList<>(modifications);
    }
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}
