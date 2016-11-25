package net.hokiegeek.android.dondeestas.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by andres on 11/24/16.
 */

public class PersonBuilder {
        protected Integer id;
        protected String name;
        protected Position position;
        protected Date tov;
        protected Boolean visible;
        protected List<Integer> whitelist;

        public PersonBuilder() {
            this.id = -1;
            this.name = "";
            this.position = new Position(0.0, 0.0, 0.0);
            this.tov = new Date();
            this.visible = false;
            this.whitelist = new ArrayList<>();
        }

        public Person build() {
            return new Person(this);
        }

        public PersonBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public PersonBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder position(double latitude, double longitude, double elevation) {
            return this.position(new Position(latitude, longitude, elevation));
        }

        public PersonBuilder position(Position position) {
            this.position = position;
            return this;
        }

        public PersonBuilder tov(Date tov) {
            this.tov = (Date)tov.clone();
            return this;
        }

        public PersonBuilder visible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        public PersonBuilder whitelist(Integer id) {
            this.whitelist.add(id);
            return this;
        }

        public PersonBuilder whitelist(List<Integer> whitelist) {
            this.whitelist = whitelist;
            return this;
        }
}
