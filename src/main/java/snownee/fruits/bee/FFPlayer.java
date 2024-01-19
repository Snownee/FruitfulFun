package snownee.fruits.bee;

import java.util.Map;
import java.util.Objects;

public interface FFPlayer {
	static FFPlayer of(Object player) {
		return (FFPlayer) player;
	}

	default String fruits$getGeneName(char codename) {
		return fruits$getGeneName(String.valueOf(codename));
	}

	String fruits$getGeneName(String codename);

	default String fruits$getGeneDesc(char codename) {
		return fruits$getGeneDesc(String.valueOf(codename));
	}

	String fruits$getGeneDesc(String codename);

	void fruits$setGeneName(String codename, GeneName name);

	Map<String, GeneName> fruits$getGeneNames();

	void fruits$setGeneNames(Map<String, GeneName> geneNames);

	void fruits$maybeInitGenes();

	record GeneName(String name, String desc) {
		public GeneName(String name, String desc) {
			this.name = Objects.requireNonNull(name);
			this.desc = Objects.requireNonNull(desc);
		}
	}
}
