package ats.blockchain.web.bean;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

public class StateInfo {
	private String status;
	private String statusDesc;
	private Set<String> preStatus;
	private boolean finalStatus;
	private boolean enableCheck;
	
	public boolean isFinalStatus() {
		return finalStatus;
	}
	public void setFinalStatus(boolean finalStatus) {
		this.finalStatus = finalStatus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public Set<String> getPreStatus() {
		return preStatus;
	}
	public void setPreStatus(String preStatus) {
		if(!StringUtils.isBlank(preStatus)) {
			this.preStatus = Sets.newHashSet(preStatus.split("\\|"));
		}
	}
	
	public boolean isEnableCheck() {
		return enableCheck;
	}
	public void setEnableCheck(boolean enableCheck) {
		this.enableCheck = enableCheck;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StateInfo [status=");
		builder.append(status);
		builder.append(", statusDesc=");
		builder.append(statusDesc);
		builder.append(", preStatus=");
		builder.append(preStatus);
		builder.append(", finalStatus=");
		builder.append(finalStatus);
		builder.append(", enableCheck=");
		builder.append(enableCheck);
		builder.append("]");
		return builder.toString();
	}
}
