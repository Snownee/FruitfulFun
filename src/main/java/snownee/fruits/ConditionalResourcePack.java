package snownee.fruits;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathResourcePack;

public abstract class ConditionalResourcePack extends PathResourcePack {

	final IModFile modFile;
	final String extension;

	public ConditionalResourcePack(String modId) {
		this(modId, "conditional");
	}

	public ConditionalResourcePack(String modId, String extension) {
		super(modId + "-" + extension, createSource(modId));
		modFile = ModList.get().getModFileById(modId).getFile();
		this.extension = extension;
	}

	private static Path createSource(String modId) {
		return ModList.get().getModFileById(modId).getFile().getFilePath().resolve("conditional");
	}

	@Nonnull
	@Override
	protected Path resolve(@Nonnull String... paths) {
		String path = String.join("/", paths);
		return modFile.findResource(extension + "/" + path);
	}

	protected abstract boolean test(String path);

	@Override
	protected boolean hasResource(String path) {
		return test(path) && super.hasResource(path);
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn, int maxDepth, Predicate<String> filter) {
		try {
			Path root = resolve(type.getDirectory(), resourceNamespace).toAbsolutePath();
			Path inputPath = root.getFileSystem().getPath(pathIn);

			return Files.walk(root).map(root::relativize).filter(path -> path.getNameCount() <= maxDepth && !path.toString().endsWith(".mcmeta") && path.startsWith(inputPath)).filter(path -> filter.test(path.getFileName().toString()))
					// It is VERY IMPORTANT that we do not rely on Path.toString as this is inconsistent between operating systems
					// Join the path names ourselves to force forward slashes
					.filter(path -> {
						String s = Joiner.on('/').join(type.getDirectory(), resourceNamespace, path);
						return test(s);
					}).map(path -> new ResourceLocation(resourceNamespace, Joiner.on('/').join(path))).collect(Collectors.toList());
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

}
