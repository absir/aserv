/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-25 上午10:33:50
 */
package com.absir.aserv.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author absir
 * 
 */
public abstract class ResourceScanner {

	/**
	 * @return the scanPath
	 */
	public abstract String getScanPath();

	/**
	 * @return the resourceProcessors
	 */
	public abstract List<ResourceProcessor> getResourceProcessors();

	/**
	 * 
	 */
	public final void startScanner() {
		if (getScanPath() == null || getResourceProcessors() == null) {
			return;
		}

		File scanFile = new File(getScanPath());
		if (scanFile.exists() && scanFile.isDirectory()) {
			doScanner(scanFile);
		}
	}

	/**
	 * @param scanFile
	 */
	protected void doScanner(File scanFile) {
		scanDirectory(scanFile, getResourceProcessors());
	}

	/**
	 * @param directory
	 * @param resourceProcessors
	 */
	private void scanDirectory(File directory, List<ResourceProcessor> resourceProcessors) {
		List<ResourceProcessor> directoryProcessors = null;
		for (ResourceProcessor resourceProcessor : resourceProcessors) {
			if (resourceProcessor.supportsDirectory(directory, this)) {
				if (directoryProcessors != null) {
					directoryProcessors.add(resourceProcessor);
				}

			} else {
				if (directoryProcessors == null) {
					directoryProcessors = new ArrayList<ResourceProcessor>(resourceProcessors.size());
					for (ResourceProcessor directoryProcessor : resourceProcessors) {
						if (directoryProcessor == resourceProcessor) {
							break;
						}

						directoryProcessors.add(directoryProcessor);
					}
				}
			}
		}

		if (directoryProcessors == null) {
			directoryProcessors = resourceProcessors;

		} else if (directoryProcessors.size() == 0) {
			return;
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				scanDirectory(file, directoryProcessors);

			} else {
				for (ResourceProcessor directoryProcessor : directoryProcessors) {
					directoryProcessor.doProcessor(file, this);
				}
			}
		}
	}
}
