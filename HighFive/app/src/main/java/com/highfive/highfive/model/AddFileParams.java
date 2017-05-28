package com.highfive.highfive.model;



import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dan on 29.05.17.
 */

public class AddFileParams {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("subject")
        @Expose
        private String subject;
        @SerializedName("deadline")
        @Expose
        private String deadline;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("offer")
        @Expose
        private String offer;
        @SerializedName("file")
        @Expose
        private List<AddFileObj> file = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOffer() {
            return offer;
        }

        public void setOffer(String offer) {
            this.offer = offer;
        }

        public List<AddFileObj> getFile() {
            return file;
        }

        public void setFile(List<AddFileObj> file) {
            this.file = file;
        }



}
