package ats.blockchain.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class AppVersion {
	public static final String getVersion() {
		return getVersionFromPackage(new AppVersion());
	}

	public static final String getVersionFromPackage(Object obj) {
		Package localPackage = obj.getClass().getPackage();
		if (localPackage == null) {
			return "";
		}
		String line = ", ";
		StringBuilder sb = new StringBuilder();
		if (localPackage.getSpecificationTitle() != null) {
			sb.append("SpecificationTitle : ").append(localPackage.getSpecificationTitle()).append(line);
		}
		if (localPackage.getSpecificationVersion() != null) {
			sb.append("SpecificationVersion : ").append(localPackage.getSpecificationVersion()).append(line);
		}
		if (localPackage.getImplementationVersion() != null) {
			sb.append("ImplementationVersion : ").append(localPackage.getImplementationVersion());
		}

		return sb.toString();
	}

	public static final String getVersionFromMainfest(String path) {
		StringBuilder sb = new StringBuilder();
		
		InputStream ins = ClassLoader.getSystemResourceAsStream(path+"META-INF/MANIFEST.MF");
		Manifest m;
		try {
			m = new Manifest(ins);
			m.getEntries().forEach((k,v)->sb.append(k).append(":").append(v.getValue(k)));
		} catch (IOException e) {
		}
		return sb.toString();
	}
}
