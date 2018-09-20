drop table basketinfo;
-- Create table

create table basketinfo
(
  basketno                VARCHAR(30) not null,
  productcode             VARCHAR(30) not null,
  SupplierCode            VARCHAR(10) not null,
  Suppliername            VARCHAR(100),
  diamondsnumber          DECIMAL(10),
  totalweight             DECIMAL(10,5),
  mimweight               DECIMAL(10,5),
  giacontrolno           varchar(20),
  gradlab                varchar(50),
  locgradlab             varchar(20),
  collectionadd          varchar(60),      
  invtymgr               varchar(10),
  sealedbagno            varchar(20),
  depaccno               varchar(12),
  reverification		varchar(20),
  ownmgr                varchar(20),
  auditor               varchar(20),
  vault                 varchar(20),
  owner                 varchar(20),
  location              varchar(20),
  auditdate             varchar(8),
  ownerdate             varchar(8),
  locdate               varchar(8),
  giaapproveddate 		varchar(8),
  result				varchar(20),
  status                varchar(4)                       
);

alter table basketinfo
  add constraint PK_basketinfo primary key (basketno);

  
  drop table diamondsinfo;
  
 create table diamondsinfo
(
	  tradeid			     int auto_increment primary key,
	  basketno               varchar(30),
	  giano                  varchar(13),
	  reqcode                varchar(10),
	  reqdate                varchar(8),
	  resdate				 varchar(8),
	  supcode                varchar(5),
	  supname                varchar(40),             
	  productcode            varchar(20),
	  shape                  varchar(2),
	  size                   decimal(5,2),
	  color                  varchar(1),
	  clarity                varchar(4),
	  cut                    varchar(2),
	  polish                 varchar(2),
	  symmetry               varchar(2),
	  status                 varchar(9),
	  origin				varchar(20),
	  minedate				varchar(8),
	  cutter				varchar(20),
	  craftsmanname			varchar(20),
	  craftsmandate   		varchar(8),
	  dealername			varchar(20),
	  dealerdate			varchar(8),
	  remark1				varchar(50),
	  remark2				varchar(50),
	  remark3				varchar(50),
	  remark4				varchar(50),
	  remark5				varchar(50)
);
alter table diamondsinfo AUTO_INCREMENT=10000;