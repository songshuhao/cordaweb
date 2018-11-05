package ats.blockchain.web.bean;

import java.util.Arrays;

public class ExportConfig {
	private String step;
	private String[] status;
	private String[] header;
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String[] getStatus() {
		return status;
	}
	public void setStatus(String[] status) {
		this.status = status;
	}
	public String[] getHeader() {
		return header;
	}
	public void setHeader(String[] header) {
		this.header = header;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExportConfig [step=");
		builder.append(step);
		builder.append(", status=");
		builder.append(Arrays.toString(status));
		builder.append(", header=");
		builder.append(Arrays.toString(header));
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
