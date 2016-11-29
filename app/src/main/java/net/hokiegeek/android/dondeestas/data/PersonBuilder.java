package net.hokiegeek.android.dondeestas.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andres on 11/24/16.
 */

public class PersonBuilder {
        protected Integer id;
        protected String name;
        protected Position position;
        protected Boolean visible;
        protected List<Integer> whitelist;
        protected List<Integer> following;

        public PersonBuilder() {
            this.id = -1;
            this.name = "";
            this.position = new Position(0.0, 0.0, 0.0);
            this.visible = false;
            this.whitelist = new ArrayList<>();
            this.following = new ArrayList<>();
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

        public PersonBuilder whitelist(Integer id) {
            this.whitelist.add(id);
            return this;
        }

        public PersonBuilder whitelist(List<Integer> whitelist) {
            this.whitelist = whitelist;
            return this;
        }

        public PersonBuilder follow(Integer id) {
            this.following.add(id);
            return this;
        }

        public PersonBuilder following(List<Integer> following) {
            this.whitelist = following;
            return this;
    }
}
