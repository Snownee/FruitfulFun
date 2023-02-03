package snownee.fruits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;

public abstract class ConditionalPackResources extends AbstractPackResources {

	private final Path source;
	private final String modId;
	private final String extension;
	final IModFile modFile;
	private final PackMetadataSection packInfo;

	public ConditionalPackResources(String modId, PackMetadataSection packInfo) {
		this(modId, packInfo, "conditional");
	}

	public ConditionalPackResources(String modId, PackMetadataSection packInfo, String extension) {
		super(new File("Dummy"));
		modFile = ModList.get().getModFileById(modId).getFile();
		this.modId = modId;
		this.extension = extension;
		this.packInfo = packInfo;
		source = createSource(modId);
	}

	private static Path createSource(String modId) {
		return ModList.get().getModFileById(modId).getFile().getFilePath().resolve("conditional");
	}

	public Path getSource() {
		return source;
	}

	@Override
	public String getName() {
		return modId + "-" + extension;
	}

	@Nonnull
	protected Path resolve(@Nonnull String... paths) {
		String path = String.join("/", paths);
		return modFile.findResource(extension + "/" + path);
	}

	protected abstract boolean test(String path);

	@Override
	protected boolean hasResource(String path) {
		return test(path) && Files.exists(resolve(path));
	}

	@Override
	protected InputStream getResource(String name) throws IOException {
		final Path path = resolve(name);
		if (!Files.exists(path))
			throw new FileNotFoundException("Can't find resource " + name + " at " + getSource());
		return Files.newInputStream(path, StandardOpenOption.READ);
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn, Predicate<ResourceLocation> filter) {
		try {
			Path root = resolve(type.getDirectory(), resourceNamespace).toAbsolutePath();
			Path inputPath = root.getFileSystem().getPath(pathIn);

			return Files.walk(root).map(root::relativize).filter(path -> {
				return !path.toString().endsWith(".mcmeta") && path.startsWith(inputPath);
			}).filter(path -> {
				return true;
			}).filter(path -> {
				String s = Joiner.on('/').join(type.getDirectory(), resourceNamespace, path);
				return test(s);
			}).map(path -> new ResourceLocation(resourceNamespace, Joiner.on('/').join(path))).collect(Collectors.toList());
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public Set<String> getNamespaces(final PackType type) {
		try {
			Path root = resolve(type.getDirectory());
			return Files.walk(root, 1).map(path -> root.relativize(path)).filter(path -> path.getNameCount() > 0) // skip the root entry
					.map(p -> p.toString().replaceAll("/$", "")) // remove the trailing slash, if present
					.filter(s -> !s.isEmpty()) //filter empty strings, otherwise empty strings default to minecraft in ResourceLocations
					.collect(Collectors.toSet());
		} catch (IOException e) {
			if (type == PackType.SERVER_DATA) //We still have to add the resource namespace if client resources exist, as we load langs (which are in assets) on server
			{
				return this.getNamespaces(PackType.CLIENT_RESOURCES);
			} else {
				return Collections.emptySet();
			}
		}
	}

	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
		if (deserializer.getMetadataSectionName().equals("pack")) {
			return (T) packInfo;
		}
		return null;
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "%s: %s", getClass().getName(), getSource());
	}

}
