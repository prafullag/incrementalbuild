package com.incrementalbuild;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 
 * @author prafulla.gupta
 *
 */
public class DirectoryChangeDetector {
	private String modulePath;
			
	private class FileChanged{
		private boolean isChanged = false;

		public boolean isChanged() {
			return isChanged;
		}

		public void setChanged(boolean isChanged) {
			this.isChanged = isChanged;
		}
	}
	
	DirectoryChangeDetector(String modulePath){
		this.modulePath = modulePath;
	}

	public boolean hasChanged() throws Exception{
		
		Path srcPath = Paths.get(modulePath + "/src");
		
		Path targetPath = Paths.get(modulePath, "/target");
		
		if(!Files.exists(targetPath)){
			return true;
		}
		
		final FileChanged fileChanged = new FileChanged();
		
		BasicFileAttributes attributes = Files.readAttributes(targetPath, BasicFileAttributes.class);
		
		final long targetFileTime = attributes.lastModifiedTime().toMillis();

		
		Files.walkFileTree(srcPath, new FileVisitor<Path>()  {

			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(attrs.lastModifiedTime().toMillis() > targetFileTime){
					try {
						fileChanged.setChanged(true);
					} catch (Exception e) {
						System.out.println("Error in detecting file change for : " + modulePath);
						e.printStackTrace();
					}
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}
			
		});
		
		return fileChanged.isChanged();
	}
}
