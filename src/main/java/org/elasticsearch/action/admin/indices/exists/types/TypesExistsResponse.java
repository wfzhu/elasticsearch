/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
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

package org.elasticsearch.action.admin.indices.exists.types;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;

import java.io.IOException;

/**
 * Whether all of the existed types exist.
 */
public class TypesExistsResponse implements ActionResponse, Streamable {

    private boolean exists;

    TypesExistsResponse() {
    }

    public TypesExistsResponse(boolean exists) {
        this.exists = exists;
    }

    public boolean exists() {
        return this.exists;
    }

    public boolean isExists() {
        return exists();
    }

    public void readFrom(StreamInput in) throws IOException {
        exists = in.readBoolean();
    }

    public void writeTo(StreamOutput out) throws IOException {
        out.writeBoolean(exists);
    }
}