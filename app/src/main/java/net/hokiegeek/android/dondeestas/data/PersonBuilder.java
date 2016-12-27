package net.hokiegeek.android.dondeestas.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by andres on 11/24/16.
 */

public class PersonBuilder {
        protected String id;
        protected String name;
        protected Position position;
        protected Boolean visible;
        protected List<String> whitelist;
        protected List<String> following;

        public PersonBuilder() {
            this.id = "";
            this.name = "";
            this.position = new Position(new Date(), 0.0, 0.0, 0.0);
            this.visible = true;
            this.whitelist = new ArrayList<>();
            this.following = new ArrayList<>();
        }

        public Person build() {
            return new Person(this);
        }

        public PersonBuilder clone() {
            PersonBuilder c = new PersonBuilder();
            c.id = this.id;
            c.name = this.name;
            c.position = this.position.clone();
            c.visible = this.visible;
            for (int i = 0; i < this.whitelist.size(); i++) {
                c.whitelist.add(this.whitelist.get(i)) ;
            }
            for (int i = 0; i < this.following.size(); i++) {
                c.following.add(this.following.get(i)) ;
            }
            return c;
        }

        public PersonBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PersonBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder position(Date tov, double latitude, double longitude, double elevation) {
            return this.position(new Position(tov, latitude, longitude, elevation));
        }

        public PersonBuilder position(Position position) {
            this.position = position;
            return this;
        }

        public PersonBuilder visible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        public PersonBuilder whitelist(String id) {
            this.whitelist.add(id);
            return this;
        }

        public PersonBuilder whitelist(List<String> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public PersonBuilder following(String id) {
            this.following.add(id);
            return this;
        }

        public PersonBuilder following(List<String> following) {
            this.following = following;
            return this;
    }
}
