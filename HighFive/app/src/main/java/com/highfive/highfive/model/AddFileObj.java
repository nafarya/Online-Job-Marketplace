package com.highfive.highfive.model;

/**
 * Created by dan on 28.05.17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddFileObj {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("path")
        @Expose
        private String path;

        public AddFileObj(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }


}
