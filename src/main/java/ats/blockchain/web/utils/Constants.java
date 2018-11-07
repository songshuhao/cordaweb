package ats.blockchain.web.utils;


public class Constants {
	public static final String SESSION_USER_ID="userid";
	public static final String SESSION_USER_PWD="userid";
	
	public static final String status_basket_into="0";
	public static final String status_basket_submit="1";
	
	public static final String status_diamonds_into="2";
	public static final String status_diamonds_sumbmit="3";
	
	public static final String status_basket_confirm_into="4";
	public static final String status_basket_confirm_submit="5";
	public static final String status_basket_gia_into="6";
	public static final String status_basket_gia_submit="7";
	
	public static final String status_basket_move_into="8";
	public static final String status_basket_move_submit="9";
	public static final String status_basket_move_valut_into="10";
	public static final String status_basket_move_valut_submit="11";
	
	//audit
	public static final String status_basket_audit_submit="12";
	public static final String status_basket_audit_auditer_into="13";
	public static final String status_basket_audit_auditer_submit="14";
	
	//transfer owner
	public static final String status_basket_transfer_into="15";
	public static final String status_basket_transfer_submit="16";
	public static final String status_basket_transfer_vault_submit="17";
	
	public static final String ROLE_AOC="OU=AOC";
	public static final String ROLE_SUPPLIER="OU=Supplier";
	public static final String ROLE_LAB="OU=Lab";
	public static final String ROLE_VAULT="OU=Vault";
	public static final String ROLE_AUDIT="OU=Auditor";
	
	//flowStep
	public static final String AOC_TO_SUPPLIER="ats";
	public static final String SUPPLIER_TO_AOC="sta";
	
	public static final String AOC_TO_GIA="atg";
	public static final String GIA_TO_AOC="gta";
	
	public static final String AOC_TO_VAULT="atv";
	public static final String VAULT_TO_AOC="vta";
	
	public static final String AOC_TO_VAULT_OWNER="atvo";
	public static final String VAULT_OWNER_TO_AOC="vota";
	
	public static final String AOC_TO_AUDIT="atau";
	public static final String AUDIT_TO_AOC="auta";
	
	public static final String FILE_TYPE_CSV=".csv";
	public static final String FILE_TYPE_XLS=".xls";
	public static final String FILE_TYPE_XLSX=".xlsx";
	
	
}
