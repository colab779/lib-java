//
//  Author: Hari Sekhon
//  Date: 2015-05-31 14:05:14 +0100 (Sun, 31 May 2015)
//
//  vim:ts=4:sts=4:sw=4:et
//
//  https://github.com/harisekhon/lib-java
//
//  License: see accompanying Hari Sekhon LICENSE file
//
//  If you're using my code you're welcome to connect with me on LinkedIn and optionally send me feedback to help improve or steer this or other code I publish
//
//  http://www.linkedin.com/in/harisekhon
//

package HariSekhon;

import static HariSekhon.Utils.*;

import java.util.ArrayList;
import java.util.Arrays;


// JUnit 3
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
// JUnit 4
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;
import static java.lang.Math.pow;

/**
 * Unit tests for HariSekhon.Utils
 */
public class UtilsTest { // extends TestCase { // JUnit 3
    /**
     * Create the test case
     *
     * @param testName name of the test case
     *
    public UtilsTest( String testName )
    {
        super( testName );
    }
	*/
	
    /**
     * @return the suite of tests being tested
     *
    public static Test suite()
    {
        return new TestSuite( UtilsTest.class );
    }
	*/
	
    // no method called should take 1 sec
    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);
    // specified in millis - this should never take even 1 second
    //@Test(timeout=1000)
    // done at global level above now

	@Test
    public void test_getStatusCode(){
    	// programs depends on this for interoperability with Nagios compatiable monitoring systems
    	assertSame("getStatus(OK)", 			0, 	getStatusCode("OK"));
    	assertSame("getStatus(WARNING)", 		1, 	getStatusCode("WARNING"));
    	assertSame("getStatus(CRITICAL)", 		2, 	getStatusCode("CRITICAL"));
    	assertSame("getStatus(UNKNOWN)", 		3, 	getStatusCode("UNKNOWN"));
    	assertSame("getStatus(DEPENDENT)", 		4, 	getStatusCode("DEPENDENT"));
    }
	
	@Test(expected=IllegalArgumentException.class)
	public void test_getStatusCode_exception(){
		getStatusCode("somethingInvalid");
	}
    
	@Test
    public void test_setStatus_getStatus_and_set_shortcuts(){
    	// start unknown - but when repeatedly testing this breaks so reset to UNKNOWN at end
	    // this isn't inheriting UNKNOWN as default from Utils any more
		setStatus("UNKNOWN");
		assertTrue(is_unknown());
    	assertTrue(getStatus().equals("UNKNOWN"));
    	warning();
    	status();
    	status2();
    	status3();
    	assertTrue(is_warning());
    	assertTrue(getStatus().equals("WARNING"));
    	
    	// shouldn't change from warning to unknown
    	unknown();
    	assertTrue(is_warning());
    	assertTrue(getStatus().equals("WARNING"));
    	
    	// critical should override unknown
    	setStatus("OK");
    	assertTrue(is_ok());
    	unknown();
    	assertTrue(is_unknown());
    	critical();
    	assertTrue(is_critical());
    	assertTrue(getStatus().equals("CRITICAL"));

    	// critical should override warning
    	setStatus("WARNING");
    	assertTrue(is_warning());
    	critical();
    	assertTrue(is_critical());
    	unknown(); // shouldn't change critical
    	assertTrue(is_critical());
    	assertTrue(getStatus().equals("CRITICAL"));
    	
    	setStatus("UNKNOWN");
    }
    
	// ====================================================================== //
	// tests both array to arraylist at same time
	@Test
	public void test_array_to_arraylist(){
	    String[] a = new String[]{"node1:9200","node2","node3:8080","node4","node5"};
	    assertArrayEquals("array_to_arraylist()", a, arraylist_to_array(array_to_arraylist(a)));
	}
	
	// ====================================================================== //
	
	@Test
	public void test_name(){
	    assertEquals("name(null)", "", name(null));
	    assertEquals("name(blank)", "", name(""));
	    assertEquals("name(blah)", "blah ", name("blah"));
	}
	
	@Test
	public void test_require_name(){
	    assertEquals("require_name(blah)", "blah", require_name("blah"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_require_name_exception_null() throws IllegalArgumentException {
	    assertEquals("require_name(null)", "", require_name(null));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_require_name_exception_blank() throws IllegalArgumentException {
	    assertEquals("require_name(blank)", "", require_name(""));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_arg_errork() throws IllegalArgumentException {
	    arg_error("blah");
	}
	
	@Test(expected=IllegalStateException.class)
	public void test_state_error() throws IllegalStateException {
	    state_error("blah");
	}
	
	// ====================================================================== //
	@Test
    public void test_check_regex(){
    	//println(check_regex("test", "test"));
    	assertTrue("check_regex(test,test)",   check_regex("test", "test"));
    	assertTrue("check_regex(test,test)",   check_regex("test", "te.t"));
    	assertFalse("check_regex(test,test2)", check_regex("test", "^est"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_check_regex_exception() throws IllegalArgumentException {
    	check_regex("test", "*est");
    }
    
    // ====================================================================== //
    @Test
    public void test_check_string(){
    	//println(check_string("test", "test"));
    	assertTrue("check_string(test,test)",   check_string("test", "test")); 			   // will use ==
    	assertTrue("check_string(test,test)",   check_string("test", "test", true));
    	assertTrue("check_string(test,test)",   check_string("test", new String("test"))); // will use .equals()
    	assertFalse("check_string(test,test2)", check_string("test", "test2"));
    }
    
    // ====================================================================== //
    @Test
    public void test_get_options(){
        get_options(new String[]{});
    }
    
    // ====================================================================== //
    @Test
    public void test_expand_units(){
    	//println(expand_units(10, "kb")); // => 10240
    	//println(10240L);
    	assertEquals("expand_units(10, KB)", 	10240L, 			expand_units(10L,  "KB"));
    	assertEquals("expand_units(10, mB)", 	10485760, 			expand_units(10,   "mB"));
    	assertEquals("expand_units(10, Gb)", 	10737418240L, 		expand_units(10L,  "Gb"));
    	assertEquals("expand_units(10, tb)", 	10995116277760L, 	expand_units(10L,  "tb"));
    	assertEquals("expand_units(10, Pb)", 	11258999068426240L, expand_units(10L,  "Pb"));
    	assertEquals("expand_units(10, KB, name)", 	1024L, 			expand_units(1L,   "KB", "name"));
    	assertEquals("expand_units(10, KB, name)", 	10240.0,  		expand_units(10.0, "KB", "name"), 	0);
    	assertEquals("expand_units(10, KB)", 	10240.0,  			expand_units(10.0, "KB"), 	0);
    }
    
    // ====================================================================== //
    @Test
    public void test_hr(){
    	hr();
    }
    
    // ====================================================================== //
    @Test
    public void test_human_units(){
    	//println(human_units(1023     * pow(1024,1)));
    	assertEquals("human_units(1023)",	"1023 bytes",	human_units(1023));
    	assertEquals("human units KB", 		"1023KB", 		human_units(1023     * pow(1024,1)));
    	assertEquals("human_units MB", 		"1023.1MB", 	human_units(1023.1   * pow(1024,2)));
    	assertEquals("human units GB",		"1023.2GB",		human_units(1023.2   * pow(1024,3)));
    	assertEquals("human units TB", 		"1023.31TB",	human_units(1023.31  * pow(1024,4)));
    	assertEquals("human units PB",		"1023.01PB",	human_units(1023.012 * pow(1024,5)));
    	assertEquals("human units EB",		"1023EB",		human_units(1023 	 * pow(1024,6)));
    	
    	assertEquals("human_units(1023)",	"1023 bytes",	human_units(1023, 		"b"));
    	assertEquals("human units KB", 		"1023KB", 		human_units(1023, 		"KB"));
    	assertEquals("human_units MB", 		"1023.1MB", 	human_units(1023.1, 	"MB"));
    	assertEquals("human units GB",		"1023.2GB",		human_units(1023.2, 	"GB"));
    	assertEquals("human units TB", 		"1023.31TB",	human_units(1023.31, 	"TB"));
    	assertEquals("human units PB",		"1023.01PB",	human_units(1023.012, 	"PB"));
    }
    
    // ====================================================================== //
    @Test
    public void test_resolve_ip(){
    	// if not on a decent OS assume I'm somewhere lame like a bank where internal resolvers don't resolve internet addresses
    	// this way my continous integration tests still run this one
    	if(isLinuxOrMac()){
    		assertEquals("resolve_ip(a.resolvers.level3.net)", 	"4.2.2.1", 	resolve_ip("a.resolvers.level3.net"));
    		assertEquals("validate_resolvable()", "4.2.2.1",  validate_resolvable("a.resolvers.level3.net"));
    	}
    	assertEquals("resolve_ip(4.2.2.1)",	   "4.2.2.1",  resolve_ip("4.2.2.1"));
    	assertEquals("validate_resolvable()",  "4.2.2.2",  validate_resolvable("4.2.2.2"));
    }
    
    // ====================================================================== //
    @Test
    public void test_strip_scheme(){
    	assertEquals("strip_scheme(file:/blah)",						"/blah",						strip_scheme("file:/blah"));
    	assertEquals("strip_scheme(file:///path/to/blah)",				"/path/to/blah",				strip_scheme("file:///path/to/blah"));
    	assertEquals("strip_scheme(http://blah)",						"blah",							strip_scheme("http://blah"));
    	assertEquals("strip_scheme(hdfs://namenode/path/to/blah)",		"namenode/path/to/blah",		strip_scheme("hdfs://namenode/path/to/blah"));
    	assertEquals("strip_scheme(hdfs://namenode:8020/path/to/blah)",	"namenode:8020/path/to/blah",	strip_scheme("hdfs://namenode:8020/path/to/blah"));
    }
    
    // ====================================================================== //
    @Test
    public void test_strip_scheme_host(){
    	assertEquals("strip_scheme_host(file:/blah)",								"/blah",			strip_scheme_host("file:/blah"));
    	assertEquals("strip_scheme_host(file:///path/to/blah)",						"/path/to/blah",	strip_scheme_host("file:///path/to/blah"));
    	assertEquals("strip_scheme_host(http://my.domain.com/blah)",				"/blah",			strip_scheme_host("http://my.domain.com/blah"));
    	assertEquals("strip_scheme_host(hdfs://nameservice1/hdfsfile)",				"/hdfsfile",		strip_scheme_host("hdfs://nameservice1/hdfsfile"));
    	assertEquals("strip_scheme_host(hdfs://namenode.domain.com:8020/hdfsfile)",	"/hdfsfile",		strip_scheme_host("hdfs://namenode.domain.com:8020/hdfsfile"));
    }
    
    // ====================================================================== //
    

    // This is highly dependent on JDK version and fails on Oracle JDK 8 in Travis, TODO: review
    /*
    @Test
    public void test_uniq_array(){
    	assertEquals("uniq_array(one,two,three,,one)",	new String[]{ "", "two", "one", "three"}, uniq_array(new String[]{"one","two","three","","one"}));
    }
    */
    
    @Test
    public void test_uniq_array_ordered(){
    	assertArrayEquals("uniq_array_ordered(one,two,three,,one)",	new String[]{ "one", "two", "three", ""}, uniq_array_ordered(new String[]{"one","two","three","","one"}));
    }
    
    @Test
    public void test_uniq_arraylist_ordered(){
        String[] a = new String[]{"one","two","three","","one"};
        String[] b = new String[]{"one","two","three",""};
        assertArrayEquals("uniq_arraylist_ordered(one,two,three,,one)",	b, arraylist_to_array(uniq_arraylist_ordered(array_to_arraylist(a))));
    }
    
    // ====================================================================== //
    //							O S   H e l p e r s
    // ====================================================================== //
    
    /*
    @Test
    public void print_java_properties(){
    	print_java_properties();
    }
    */
    //import static org.hamcrest.CoreMatchers.
    @Test
    public void test_getOS(){
    	assertTrue("getOS()", getOS().matches(".*(?:Linux|Mac|Windows).*"));
    }
    
    @Test
    public void test_isOS(){
    	assertTrue(isOS(System.getProperty("os.name")));
    }
    
    @Test
    public void test_isLinux(){
    	if(isLinux()){
    		assertEquals("isLinux()", "Linux", getOS());
    		linux_only();
    	}
    }
    
    @Test
    public void test_isMac(){
    	if(isMac()){
    		assertEquals("isMac()", "Mac OS X", getOS());
    		mac_only();
    	}
    }
    
    @Test
    public void test_isLinuxOrMac(){
    	if(isLinuxOrMac()){
    		assertTrue("isLinuxOrMac()", getOS().matches("Linux|Mac OS X"));
    		linux_mac_only();
    	}
    }
    
    // ====================================================================== //
    // 			   V a l i d a t i o n    M e t h o d s
    // ====================================================================== //
    
    @Test
    public void test_isAlNum(){
    	assertTrue("isAlNum(ABC123efg)", isAlNum("ABC123efg"));
    	assertTrue("isAlNum(0)", 		 isAlNum("0"));
    	assertFalse("isAlNum(1.2)",		 isAlNum("1.2"));
    	assertFalse("isAlNum(\"\")",     isAlNum(""));
    	assertFalse("isAlNum(hari@domain.com)",     isAlNum("hari@domain.com"));
    }
    
    @Test
    public void test_validate_alnum(){
    	assertEquals("validate_alnum(Alnum2Test99, alnum test)", "Alnum2Test99", validate_alnum("Alnum2Test99", "alnum test"));
    	assertEquals("validate_alnum(0, alnum zero)", "0", validate_alnum("0", "alnum zero"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isAwsAccessKey(){
    	assertTrue(isAwsAccessKey(repeat_string("A", 20)));
    	assertTrue(isAwsAccessKey(repeat_string("1",20)));
    	assertTrue(isAwsAccessKey(repeat_string("A1",10)));
    	assertFalse(isAwsAccessKey(repeat_string("@",20)));
    	assertFalse(isAwsAccessKey(repeat_string("A",40)));
    }
    
    @Test
    public void test_isAwsSecretKey(){
    	assertTrue(isAwsSecretKey(repeat_string("A",40)));
    	assertTrue(isAwsSecretKey(repeat_string("1",40)));
    	assertTrue(isAwsSecretKey(repeat_string("A1",20)));
    	assertFalse(isAwsSecretKey(repeat_string("@",40)));
    	assertFalse(isAwsSecretKey(repeat_string("A",20)));
    }
    
    @Test
    public void test_validate_aws_access_key(){
    	assertEquals("validate_aws_access_key(Ax20)", repeat_string("A", 20), validate_aws_access_key(repeat_string("A",20)));
    }
    
    @Test
    public void test_validate_aws_bucket(){
    	assertEquals("validate_aws_bucket(BucKeT63)", "BucKeT63", validate_aws_bucket("BucKeT63"));
    }
    
    @Test
    public void test_validate_aws_secret_key(){
    	assertEquals("validate_aws_secret_key(BucKeT63)", repeat_string("A", 40), validate_aws_secret_key(repeat_string("A", 40)));
    }
    
    
    // ====================================================================== //
    @Test
    public void test_isChars(){
    	assertTrue(isChars("Alpha-01_", "A-Za-z0-9_-"));
    	assertFalse(isChars("Alpha-01_*", "A-Za-z0-9_-"));
    }
    
    @Test
    public void test_validate_chars(){
    	assertEquals("validate_chars(...)", "log_date=2015-05-23_10", validate_chars("log_date=2015-05-23_10", "validate chars", "A-Za-z0-9_=-"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isCollection(){
    	assertTrue(isCollection("students.grades"));
    	assertFalse(isCollection("wrong@.grades"));
    }

    @Test
    public void test_validate_collection(){
    	assertEquals("validate_collection(students.grades)", "students.grades", validate_collection("students.grades"));
    	assertEquals("validate_collection(students.grades, name)", "students.grades", validate_collection("students.grades", "name"));
    }

    // ====================================================================== //
    @Test
    public void test_isDatabaseName(){
    	assertTrue(isDatabaseName("mysql1"));
    	assertFalse(isDatabaseName("my@sql"));
    }

    @Test
    public void test_validate_database(){
    	assertEquals("validate_database(mysql)", "mysql", validate_database("mysql"));
    	assertEquals("validate_database(mysql, MySQL)", "mysql", validate_database("mysql", "MySQL"));
    }

    // ====================================================================== //
    @Test
    public void test_isDatabaseColumnName(){
    	assertTrue(isDatabaseColumnName("myColumn_1"));
    	assertFalse(isDatabaseColumnName("'column'"));
    }

    @Test
    public void test_validate_database_columnname(){
    	assertEquals("validate_database_columnname(myColumn_1)", "myColumn_1", validate_database_columnname("myColumn_1"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isDatabaseFieldName(){
    	assertTrue(isDatabaseFieldName("2"));
    	assertTrue(isDatabaseFieldName("age"));
    	assertTrue(isDatabaseFieldName("count(*)"));
    	assertFalse(isDatabaseFieldName("@something"));
    }
    
    @Test
    public void test_validate_database_fieldname(){
    	assertEquals("validate_database_fieldname(age)", "age", validate_database_fieldname("age"));
    	assertEquals("validate_database_fieldname(10)", "10", validate_database_fieldname("10"));
    	assertEquals("validate_database_fieldname(count(*))", "count(*)", validate_database_fieldname("count(*)"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isDatabaseTableName(){
    	assertTrue(isDatabaseTableName("myTable_1"));
    	assertTrue(isDatabaseTableName("default.myTable_1", true));
    	assertFalse(isDatabaseTableName("'table'"));
    	assertFalse(isDatabaseTableName("default.myTable_1", false));
    	assertFalse(isDatabaseTableName("default.myTable_1"));
    }
    
    @Test
    public void test_validate_database_tablename(){
    	assertEquals("validate_database_tablename(myTable)", "myTable", validate_database_tablename("myTable"));
    	assertEquals("validate_database_tablename(myTable, Hive)", "myTable", validate_database_tablename("myTable", "Hive"));
    	assertEquals("validate_database_tablename(default.myTable, Hive, true)", "default.myTable", validate_database_tablename("default.myTable", "Hive", true));
    }
    
    // ====================================================================== //
    @Test
    public void test_isDatabaseViewName(){
    	assertTrue(isDatabaseViewName("myView_1"));
    	assertTrue(isDatabaseViewName("default.myView_1", true));
    	assertFalse(isDatabaseViewName("'view'"));
    	assertFalse(isDatabaseViewName("default.myView_1", false));
    	assertFalse(isDatabaseViewName("default.myView_1"));
    }
    
    @Test
    public void test_validate_database_viewname(){
    	assertEquals("validate_database_viewname(myView)", "myView", validate_database_viewname("myView"));
    	assertEquals("validate_database_viewname(myView, Hive)", "myView", validate_database_viewname("myView", "Hive"));
    	assertEquals("validate_database_viewname(default.myView, Hive, true)", "default.myView", validate_database_viewname("default.myView", "Hive", true));
    }
    
    // ====================================================================== //
    
    @Test
    public void test_isDirname(){
    	assertTrue("isDirname(test_Dir)", isDirname("test_Dir"));
    	assertTrue("isDirname(/tmp/test)", isDirname("/tmp/test"));
    	assertTrue("isDirname(./test)", isDirname("./test"));
    	assertFalse("isDirname(@me)", isDirname("@me"));
    }
    
    @Test
    public void test_validate_dirname(){
    	assertEquals("validate_dirname(./src)", 	"./src", 	validate_dirname("./src", "dirname"));
    	assertEquals("validate_dirname(./src, true)", 	"./src", 	validate_dirname("./src", "dirname", true));
    	assertEquals("validate_dirname(./src, true, true)", 	"./src", 	validate_dirname("./src", "dirname", true, true));
    	assertEquals("validate_dirname(/etc)", 	"/etc", 	validate_dirname("/etc", "dirname"));
    	assertEquals("validate_dirname(/etc/)", 	"/etc/", 	validate_dirname("/etc/", "dirname"));
    	assertEquals("validate_dirname(b@dDir)", 	null,   	validate_dirname("b@dDir", "invalid dir", true));
    }
    
	@Test
	public void test_validate_directory(){
		if(isLinuxOrMac()){
			assertEquals("validate_directory(./src)", 	"./src", 	validate_directory("./src", "directory"));
			assertEquals("validate_directory(./src, true)", 	"./src", 	validate_directory("./src", "directory", true));
			assertEquals("validate_directory(./src, true, true)", 	"./src", 	validate_directory("./src", "directory", true, true));
			assertEquals("validate_directory(/etc)", 	"/etc", 	validate_directory("/etc", "directory"));
			assertEquals("validate_directory(/etc/)", 	"/etc/", 	validate_directory("/etc/", "directory"));
		}
		assertEquals("validate_directory(b@dDir)", 	null,   	validate_directory("b@dDir", "invalid dir", true));
	}

    @Test
    public void test_validate_dir(){
    	if(isLinuxOrMac()){
	    	assertEquals("validate_dir(./src)", 	"./src", 	validate_dir("./src", "directory"));
	    	assertEquals("validate_dir(./src, true)", 	"./src", 	validate_dir("./src", "directory", true));
	    	assertEquals("validate_dir(./src, true, true)", 	"./src", 	validate_dir("./src", "directory", true, true));
	    	assertEquals("validate_dir(/etc)", 		"/etc", 	validate_dir("/etc", "dir"));
	    	assertEquals("validate_dir(/etc/)", 	"/etc/", 	validate_dir("/etc/", "dir"));
    	}
    	assertEquals("validate_dir(b@dDir)", 	null,   	validate_dir("b@dDir", "invalid dir", true));
    }
    
    // ====================================================================== //
    @Test
    public void test_validate_double(){
        validate_double(2.0, "two", 2, 3);
        validate_double(3.0, "three", 2, 3);
        validate_double("2.1", "two string", 2, 3);
        validate_float(2.0f, "two", 2f, 3f);
        validate_float("2.0", "two string", 2f, 3f);
        validate_long(2L,   "two", 2L, 3L);
        validate_long("2", "two string", 2L, 3L);
        validate_int(2,   "two", 2, 3);
        validate_int("2", "two string", 2, 3);
    }
    // ====================================================================== //
    @Test
    public void test_isDomain(){
    	assertTrue(isDomain("localDomain"));
    	assertTrue(isDomain("harisekhon.com"));
    	assertTrue(isDomain("1harisekhon.com"));
    	assertTrue(isDomain("com"));
    	assertTrue(isDomain(repeat_string("a",63) + ".com"));
    	assertFalse(isDomain(repeat_string("a",64) + ".com"));
    	assertFalse(isDomain("harisekhon")); // not a valid TLD
    }
    
    @Test
    public void test_validate_domain(){
    	assertEquals("validate_domain(harisekhon.com)", "harisekhon.com", validate_domain("harisekhon.com"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isDomainStrict(){
    	assertFalse(isDomainStrict("com"));
    	assertTrue(isDomainStrict("domain.com"));
    	assertTrue(isDomainStrict("123domain.com"));
    	assertTrue(isDomainStrict("domain1.com"));
    	assertTrue(isDomainStrict("domain.local"));
    	assertTrue(isDomainStrict("domain.localDomain"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isDnsShortName(){
    	assertTrue(isDnsShortName("myHost"));
    	assertFalse(isDnsShortName("myHost.domain.com"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isEmail(){
    	assertTrue(isEmail("hari'sekhon@gmail.com"));
    	assertTrue(isEmail("hari@LOCALDOMAIN"));
    	assertFalse(isEmail("harisekhon"));
    }
    
    @Test
    public void test_validate_email(){
    	assertEquals("validate_email(hari@domain.com)", "hari@domain.com", validate_email("hari@domain.com"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isFileName(){
    	assertTrue(isFilename("some_File.txt"));
    	assertTrue(isFilename("/tmp/test"));
    	assertFalse(isFilename("@me"));
    }
    
    @Test
    public void test_validate_filename(){
    	assertEquals("validate_filename(./pom.xml)", "./pom.xml", validate_filename("./pom.xml"));
    	assertEquals("validate_filename(/etc/passwd)", "/etc/passwd", validate_filename("/etc/passwd"));
    	assertEquals("validate_filename(/etc/passwd, name)", "/etc/passwd", validate_filename("/etc/passwd", "name"));
    	assertEquals("validate_filename(/etc/passwd/)", null, validate_filename("/etc/passwd/", "/etc/passwd/", true));
    	assertEquals("validate_filename(/nonexistentfile)", "/nonexistentfile", validate_filename("/nonexistentfile", "nonexistentfile", true));
    }
    
    @Test
    public void test_validate_file(){
    	assertEquals("validate_file(./pom.xml)", "./pom.xml", validate_file("./pom.xml"));
    	assertEquals("validate_file(./pom.xml)", "./pom.xml", validate_file("./pom.xml", "name"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isFqdn(){
    	assertTrue(isFqdn("hari.sekhon.com"));
    	assertFalse(isFqdn("hari@harisekhon.com"));
    }

    @Test
    public void test_validate_fqdn(){
        assertEquals("validate_fqdn(www.harisekhon.com)", "www.harisekhon.com", validate_fqdn("www.harisekhon.com"));
        // permissive because of short tld style internal domains
        assertEquals("validate_fqdn(myhost.local, name)", "myhost.local", validate_fqdn("myhost.local", "name"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isHex(){
    	assertTrue(isHex("0xAf09b"));
    	assertFalse(isHex("9"));
    	assertFalse(isHex("0xhari"));
    }
    
    // ====================================================================== //
    //@Test(timeout=1000)
    @Test
    public void test_isHost(){
    	assertTrue(isHost("harisekhon.com"));
    	assertTrue(isHost("harisekhon"));
    	assertTrue(isHost("10.10.10.1"));
    	assertTrue(isHost("10.10.10.10"));
    	assertTrue(isHost("10.10.10.100"));
    	assertTrue(isHost("10.10.10.0"));
    	assertTrue(isHost("10.10.10.255"));
    	assertFalse(isHost("10.10.10.256"));
    	assertFalse(isHost(repeat_string("a", 256)));
    }
    
    @Test
    public void test_validate_host(){
    	assertEquals("validate_host(10.10.10.10)", "10.10.10.10", validate_host("10.10.10.10", "name"));
    	assertEquals("validate_host(myHost)",	   "myHost",	  validate_host("myHost"));
    	assertEquals("validate_host(myHost.myDomain.com)",	"myHost.myDomain.com",	validate_host("myHost.myDomain.com"));
    }
    
    @Test
    public void test_validate_hosts(){
        String[] a = new String[]{"node1:9200","node2:80","node3","node4","node5"};
        String[] b = new String[]{"node1:9200","node2:80","node3:8080","node4:8080","node5:8080"};
        assertArrayEquals("validate_hosts()", b, validate_hosts(a, "8080"));
        assertArrayEquals("validate_hosts()", b, validate_hosts(a, 8080));
        assertArrayEquals("validate_hosts()", b, arraylist_to_array(validate_hosts(array_to_arraylist(a), "8080")));
        assertArrayEquals("validate_hosts()", b, arraylist_to_array(validate_hosts(array_to_arraylist(a), 8080)));
        assertEquals("validate_hosts(myHost)",	   "myHost:8080",	  validate_hosts("myHost", 8080));
        assertEquals("validate_hosts(myHost)",	   "myHost:8081,myHost2:9200",	  validate_hosts("myHost,myHost2:9200", 8081));
        assertEquals("validate_hosts(myHost.myDomain.com)",	"myHost.myDomain.com:8080",	validate_hosts("myHost.myDomain.com", "8080"));
    }
    
    @Test
    public void test_validate_hostport(){
        assertEquals("validate_hostport(10.10.10.10:8080)", "10.10.10.10:8080", validate_hostport("10.10.10.10:8080", "name", true));
        assertEquals("validate_hostport(myHost)",	   "myHost",	  validate_hostport("myHost"));
        assertEquals("validate_hostport(myHost2)",	   "myHost2",	  validate_hostport("myHost2", "name2"));
        assertEquals("validate_hostport(myHost.myDomain.com)",	"myHost.myDomain.com",	validate_hostport("myHost.myDomain.com", "fqdn_host", false, true));
    }
    
    /*
    @Test
    public void test_validate_host_port_user_password(){
    }
    */
    
    // ====================================================================== //
    @Test
    public void test_isHostname(){
    	assertTrue(isHostname("harisekhon.com"));
    	assertTrue(isHostname("harisekhon"));
    	assertTrue(isHostname("a"));
    	assertTrue(isHostname("harisekhon1.com"));
    	assertFalse(isHostname("1"));
    	assertFalse(isHostname("1harisekhon.com"));
    	assertTrue(isHostname(repeat_string("a",63)));
    	assertFalse(isHostname(repeat_string("a",64)));
    }
    
    @Test
    public void test_validate_hostname(){
    	assertEquals("validate_hostname(myHost)",	   "myHost",	  validate_hostname("myHost", "name"));
    	assertEquals("validate_hostname(myHost.myDomain.com)",	"myHost.myDomain.com",	validate_hostname("myHost.myDomain.com"));
    	assertEquals("validate_hostname(harisekhon1.com)",	"harisekhon1.com",	validate_hostname("harisekhon1.com"));
    	assertEquals("validate_hostname(repeat_string(a))",	repeat_string("a",63),	validate_hostname(repeat_string("a",63)));
    }
    
    // ====================================================================== //
    @Test
    public void test_isInterface(){
    	assertTrue(isInterface("eth0"));
    	assertTrue(isInterface("bond3"));
    	assertTrue(isInterface("lo"));
    	assertFalse(isInterface("b@interface"));
    }
    
    @Test
    public void test_validate_interface(){
    	assertEquals("valdiate_interface(eth0)",  "eth0",  validate_interface("eth0"));
    	assertEquals("valdiate_interface(bond3)", "bond3", validate_interface("bond3"));
    	assertEquals("valdiate_interface(lo)",    "lo",    validate_interface("lo"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isIP(){
    	assertTrue("isIP(10.10.10.1)",        isIP("10.10.10.1"));
    	assertTrue("isIP(10.10.10.10)",       isIP("10.10.10.10"));
    	assertTrue("isIP(10.10.10.100)",      isIP("10.10.10.100"));
    	assertTrue("isIP(254.0.0.254)",       isIP("254.0.0.254"));
    	assertTrue("isIP(255.255.255.254)",   isIP("255.255.255.254"));
    	assertTrue("isIP(10.10.10.0)",        isIP("10.10.10.0"));
    	assertTrue("isIP(10.10.10.255)",      isIP("10.10.10.255"));
    	assertFalse("isIP(10.10.10.256)",     isIP("10.10.10.256"));
    	assertFalse("isIP(x.x.x.x)",          isIP("x.x.x.x"));
    }

    @Test
    public void test_validate_ip(){
        assertEquals("validate_ip(validate_ip(10.10.10.1)",     "10.10.10.1",   validate_ip("10.10.10.1", "name"));
        assertEquals("validate_ip(validate_ip(10.10.10.10)",    "10.10.10.10",  validate_ip("10.10.10.10"));
        assertEquals("validate_ip(validate_ip(10.10.10.100)",   "10.10.10.100", validate_ip("10.10.10.100"));
        assertEquals("validate_ip(validate_ip(10.10.10.254)",   "10.10.10.254", validate_ip("10.10.10.254"));
        assertEquals("validate_ip(validate_ip(254.0.0.254)",    "254.0.0.254",  validate_ip("254.0.0.254"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isKrb5Princ(){
    	assertTrue(isKrb5Princ("tgt/HARI.COM@HARI.COM"));
    	assertTrue(isKrb5Princ("hari"));
    	assertTrue(isKrb5Princ("hari@HARI.COM"));
    	assertTrue(isKrb5Princ("hari/my.host.local@HARI.COM"));
    	assertTrue(isKrb5Princ("cloudera-scm/admin@REALM.COM"));
    	assertTrue(isKrb5Princ("cloudera-scm/admin@SUB.REALM.COM"));
    	assertTrue(isKrb5Princ("hari@hari.com"));
    	assertFalse(isKrb5Princ("hari$HARI.COM"));
    }
    
    @Test
    public void test_validate_krb5_princ(){
    	assertEquals("validate_krb5_princ(tgt/HARI.COM@HARI.COM)", "tgt/HARI.COM@HARI.COM", validate_krb5_princ("tgt/HARI.COM@HARI.COM", "name"));
    	assertEquals("validate_krb5_princ(hari)", "hari", validate_krb5_princ("hari"));
    	assertEquals("validate_krb5_princ(hari@HARI.COM)", "hari@HARI.COM", validate_krb5_princ("hari@HARI.COM"));
    	assertEquals("validate_krb5_princ(hari/my.host.local@HARI.COM)", "hari/my.host.local@HARI.COM", validate_krb5_princ("hari/my.host.local@HARI.COM"));
    	assertEquals("validate_krb5_princ(cloudera-scm/admin@REALM.COM)", "cloudera-scm/admin@REALM.COM", validate_krb5_princ("cloudera-scm/admin@REALM.COM"));
    	assertEquals("validate_krb5_princ(cloudera-scm/admin@SUB.REALM.COM)", "cloudera-scm/admin@SUB.REALM.COM", validate_krb5_princ("cloudera-scm/admin@SUB.REALM.COM"));
    	assertEquals("validate_krb5_princ(hari@hari.com)", "hari@hari.com", validate_krb5_princ("hari@hari.com"));
    }
    
    @Test
    public void test_validate_krb5_realm(){
    	assertEquals("validate_krb5_realm(harisekhon.com)", "harisekhon.com", validate_krb5_realm("harisekhon.com"));
    }

    // ====================================================================== //
    @Test
    public void test_isLabel(){
    	assertTrue(isLabel("st4ts used_(%%)"));
    	assertFalse(isLabel("b@dlabel"));
    }
    
    @Test
    public void test_validate_label(){
        assertEquals("validate_label(st4ts_used (%%))", "st4ts_used (%%)", validate_label("st4ts_used (%%)"));
        assertEquals("validate_label(st4ts_used (%%))", "st4ts_used (%%)", validate_label("st4ts_used (%%)"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isLdapDn(){
    	assertTrue(isLdapDn("uid=hari,cn=users,cn=accounts,dc=local"));
    	assertFalse(isLdapDn("hari@LOCAL"));
    }
    
    @Test
    public void test_validate_ldap_dn(){
    	assertEquals("validate_ldap_dn(uid=hari,cn=users,cn=accounts,dc=local)", "uid=hari,cn=users,cn=accounts,dc=local", validate_ldap_dn("uid=hari,cn=users,cn=accounts,dc=local"));
    	assertEquals("validate_ldap_dn(uid=hari,cn=users,cn=accounts,dc=local, name)", "uid=hari,cn=users,cn=accounts,dc=local", validate_ldap_dn("uid=hari,cn=users,cn=accounts,dc=local", "name"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isNagiosUnit(){
    	assertTrue(isNagiosUnit("s"));
    	assertTrue(isNagiosUnit("ms"));
    	assertTrue(isNagiosUnit("us"));
    	assertTrue(isNagiosUnit("b"));
    	assertTrue(isNagiosUnit("Kb"));
    	assertTrue(isNagiosUnit("Mb"));
    	assertTrue(isNagiosUnit("Gb"));
    	assertTrue(isNagiosUnit("Tb"));
    	assertTrue(isNagiosUnit("c"));
    	assertTrue(isNagiosUnit("%"));
    	assertFalse(isNagiosUnit("Kbps"));
    }
    
    @Test
    public void test_validate_units(){
        assertEquals("validate_units(s)",   "s",    validate_units("s", "name"));
        assertEquals("validate_units(ms)",  "ms",   validate_units("ms"));
        assertEquals("validate_units(us)",  "us",   validate_units("us"));
        assertEquals("validate_units(B)",   "B",    validate_units("B"));
        assertEquals("validate_units(KB)",  "KB",   validate_units("KB"));
        assertEquals("validate_units(MB)",  "MB",   validate_units("MB"));
        assertEquals("validate_units(GB)",  "GB",   validate_units("GB"));
        assertEquals("validate_units(TB)",  "TB",   validate_units("TB"));
        assertEquals("validate_units(c)",   "c",    validate_units("c"));
        assertEquals("validate_units(%%)",  "%",    validate_units("%"));
    }
    
    // ====================================================================== //
    // TODO: validate_node_list / validate_nodeport_list
    
    @Test
    public void test_validate_node_list(){
        assertEquals("validate_node_list(String)", "node1,node2,node3,node4,node5", validate_node_list("node1 ,node2 node3  node4, node5"));
        String[] a = new String[]{"node1","node2","node3","node4","node5"};
        assertArrayEquals("validate_node_list(ArrayList<String>)",  arraylist_to_array(new ArrayList<String>(Arrays.asList(a))), arraylist_to_array(validate_node_list(array_to_arraylist(a))));
        assertArrayEquals("validate_node_list(String[])",  a, validate_node_list(a));
    }
    
    @Test
    public void test_validate_nodeport_list(){
        assertEquals("validate_nodeport_list(String)", "node1:9200,node2,node3:8080,node4,node5", validate_nodeport_list("node1:9200 ,node2 node3:8080 node4, node5"));
        String[] a = new String[]{"node1:9200","node2","node3:8080","node4","node5"};
        assertArrayEquals("validate_nodeport_list(ArrayList<String>)", arraylist_to_array(new ArrayList<String>(Arrays.asList(a))), arraylist_to_array(validate_nodeport_list(array_to_arraylist(a))));
        assertArrayEquals("validate_nodeport_list(String[])", a, validate_nodeport_list(a));
    }
    
    // ====================================================================== //
    @Test
    public void test_isNoSqlKey(){
    	assertTrue(isNoSqlKey("HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc"));
    	assertFalse(isNoSqlKey("HariSekhon@check_riak_write.pl"));
    }
    
    @Test
    public void test_validate_nosql_key(){
        assertEquals("validate_nosql_key(HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc)", "HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc", validate_nosql_key("HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc"));
        assertEquals("validate_nosql_key(HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc, name)", "HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc", validate_nosql_key("HariSekhon:check_riak_write.pl:riak1:1385226607.02182:20abc", "name"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isPort(){
    	assertTrue(isPort("80"));
    	assertTrue(isPort(80));
    	assertTrue(isPort(65535));
    	assertFalse(isPort(65536));
    	assertFalse(isPort("a"));
    	assertFalse(isPort("-1"));
    	assertFalse(isPort("0"));
    	assertFalse(isPort(-1));
    	assertFalse(isPort(0));
    }

    @Test
    public void test_validate_port(){
        assertEquals("validate_port(1)",     1,         validate_port(1, "name"));
        assertEquals("validate_port(80)",    80,        validate_port(80));
        assertEquals("validate_port(65535)", 65535,     validate_port(65535));
        assertEquals("validate_port(1)",     "1",       validate_port("1"));
        assertEquals("validate_port(80)",    "80",      validate_port("80"));
        assertEquals("validate_port(65535)", "65535" ,  validate_port("65535"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isProcessName(){
    	assertTrue(isProcessName("../my_program"));
    	assertTrue(isProcessName("ec2-run-instances"));
    	assertTrue(isProcessName("sh <defunct>"));
    	assertFalse(isProcessName("./b@dfile"));
    	assertFalse(isProcessName("[init] 3"));
    }

    @Test
    public void test_validate_process_name(){
        assertEquals("validate_process_name(../my_program)", "../my_program", validate_process_name("../my_program", "name"));
        assertEquals("validate_process_name(ec2-run-instances)", "ec2-run-instances", validate_process_name("ec2-run-instances"));
        assertEquals("validate_process_name(sh <defunct>)", "sh <defunct>", validate_process_name("sh <defunct>"));
    }
    
    // ====================================================================== //
    @Test
    public void test_validate_program_path(){
        assertEquals("validate_program_path()", "/bin/sh", validate_program_path("/bin/sh", "sh"));
        assertEquals("validate_program_path()", "/bin/sh", validate_program_path("/bin/sh", "shell", "sh"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isRegex(){
    	assertTrue(isRegex(".*"));
    	assertTrue(isRegex("(.*)"));
    	assertFalse(isRegex("(.*"));
    }
    
    @Test
    public void test_validate_regex(){
    	assertEquals("validate_regex(some[Rr]egex.*(capture))", "some[Rr]egex.*(capture)",	validate_regex("some[Rr]egex.*(capture)", "myRegex"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isUrl(){
    	assertTrue(isUrl("www.google.com"));
    	assertTrue(isUrl("http://www.google.com"));
    	assertTrue(isUrl("https://gmail.com"));
    	assertFalse(isUrl("1"));
    	assertTrue(isUrl("http://cdh43:50070/dfsnodelist.jsp?whatNodes=LIVE"));
    }
    
    @Test
    public void test_validate_url(){
        assertEquals("validate_url(www.google.com)",        "http://www.google.com", validate_url("www.google.com", "name"));
        assertEquals("validate_url(http://www.google.com)", "http://www.google.com", validate_url("http://www.google.com"));
        assertEquals("validate_url(http://gmail.com)",      "http://gmail.com",      validate_url("http://gmail.com"));
        assertEquals("validate_url(http://cdh43:50070/dfsnodelist.jsp?whatNodes=LIVE)",      "http://cdh43:50070/dfsnodelist.jsp?whatNodes=LIVE",      validate_url("http://cdh43:50070/dfsnodelist.jsp?whatNodes=LIVE"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isUrlPathSuffix(){
    	assertTrue(isUrlPathSuffix("/"));
    	assertTrue(isUrlPathSuffix("/?var=something"));
    	assertTrue(isUrlPathSuffix("/dir1/file.php?var=something+else&var2=more%20stuff"));
    	assertTrue(isUrlPathSuffix("/*"));
    	assertTrue(isUrlPathSuffix("/~hari"));
    	assertFalse(isUrlPathSuffix("hari"));
    }
    
    @Test
    public void test_validate_url_path_suffix(){
    	assertEquals("validate_url_path_suffix(/)", "/", validate_url_path_suffix("/", "name"));
    	assertEquals("validate_url_path_suffix(/?var=something)", "/?var=something", validate_url_path_suffix("/?var=something"));
    	assertEquals("validate_url_path_suffix(/dir1/file.php?var=something+else&var2=more%20stuff)", "/dir1/file.php?var=something+else&var2=more%20stuff", validate_url_path_suffix("/dir1/file.php?var=something+else&var2=more%20stuff"));
    	assertEquals("validate_url_path_suffix(/*)", "/*", validate_url_path_suffix("/*"));
    	assertEquals("validate_url_path_suffix(/~hari)", "/~hari", validate_url_path_suffix("/~hari"));
    }
    
    // ====================================================================== //
    @Test
    public void test_isUser(){
    	assertTrue(isUser("hadoop"));
    	assertTrue(isUser("hari1983"));
    	assertTrue(isUser("cloudera-scm"));
    	assertTrue(isUser("cloudera-scm"));
    	assertFalse(isUser("-hari"));
    	assertFalse(isUser("1983hari"));
    }
    
    @Test
    public void test_validate_user(){
        assertEquals("validate_user(hadoop, name)", "hadoop", validate_user("hadoop", "name"));
        assertEquals("validate_user(hari1)", "hari1", validate_user("hari1"));
    }
    
    @Test
    public void test_validate_exists(){
    	if(isLinuxOrMac()){
    		assertEquals("validate_user_exists(root)", "root", validate_user("root"));
    	}
    }
    
    
    // ====================================================================== //
    @Test
    public void test_isVersion(){
    	assertTrue(isVersion("1"));
    	assertTrue(isVersion("2.1.2"));
    	assertTrue(isVersion("2.2.0.4"));
    	assertTrue(isVersion("3.0"));
    	assertFalse(isVersion("a"));
    	assertFalse(isVersion("3a"));
    	assertFalse(isVersion("1.0-2"));
    	assertFalse(isVersion("1.0-a"));
    }
    
    // ====================================================================== //
    
    @Test
    public void test_validate_database_query_select_show(){
    	assertEquals("validate_database_query_select_show(SELECT count(*) from database.table)", "SELECT count(*) from database.table", validate_database_query_select_show("SELECT count(*) from database.table"));
    }

    @Test
    public void test_validate_password(){
        assertEquals("validate_password(wh@tev3r)", "wh@tev3r", validate_password("wh@tev3r"));
    }
    
    @Test
    public void test_validate_vlog(){
        vlog("vlog");
        vlog("vlog2");
        vlog("vlog3");
        vlog_options("vlog_option", "myOpt");
        vlog_options_bool("vlog_option_bool", true);
        vlog_options_bool("vlog_option_bool", false);
    }
    
    // ====================================================================== //
    
    /* re-enable later
    @Test
    public void test_user_exists(){
    	assertTrue(user_exists("root"));
    	assertFalse(user_exists("nonexistent"));
    }
     */

    @Test
    public void test_verbose(){
        setVerbose(2);
        assertEquals("getVerbose() 2", 2, getVerbose());
        setVerbose(1);
        assertEquals("getVerbose() 1", 1, getVerbose());
        setVerbose(3);
        assertEquals("getVerbose() 3", 3, getVerbose());
    }
    
    
    @Test
    public void test_version(){
    	version();
    }
    
    @Test
    public void test_version_string(){
    	version_string();
    }
    
    // ====================================================================== //    
    @Test
    public void test_validate_which(){
    	if(isLinuxOrMac()){
    		assertEquals("which(sh)",                           "/bin/sh",      which("sh"));
    		assertEquals("which(/bin/bash)",                    "/bin/bash",    which("/bin/bash"));
    	}
        assertEquals("which(/explicit/nonexistent/path",    null,           which("/explicit/nonexistent/path"));
        assertEquals("which(nonexistentprogram",            null,           which("nonexistentprogram"));
    }
    
    // ====================================================================== //
    /*
    @Test
    public void test_sec2min(){
    	assertEquals("sec2min(65)",	"1:05", sec2min(65));
    	assertEquals("sec2min(30)", "0:30",   sec2min(30));
    	assertEquals("sec2min(3601)", "60:01", sec2min(3601));
    	assertEquals("sec2min(0)", "0:00", sec2min(0));
    	
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_sec2min_exception_negative(){
    	sec2min("-1");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_sec2min_exception_nonnumeric(){
    	sec2min("aa");
    }
     */
    
    // ====================================================================== //
    
    // @Test(expected= IndexOutOfBoundsException.class)
    // public void test_validate_* {
    // change validate_* to throwing exceptions for better testing and wrap in a generic exception catcher in Nagios code to convert to one liners
    
    //@Test
    // public void test_validate* {
    // 		try {
    //     		...
    //     		fail("Expected a BlahException to be thrown");
    // 		} catch (BlahException e) {
    //     		assertThat(e.getMessage(), is("some message"));
    // 		}
    // }
    
    //@Ignore("Test disabled, re-enable later")
    // or use this more flexible one which allows use of matchers such as .containsString()
    //@Rule
    //public ExpectedException thrown = ExpectedException.none();
    
    //@Ignore("Test is ignored as a demonstration")
    //@Test
    //public void validate_*() throw IndexOutOfBoundsException {
    //	thrown.expect(IndexOutOfBoundsException.class);
    //	thrown.expectMessage("exact msg");
    //	thrown.expectMessage(JUnitMatchers.containsString("partial msg"));
    //	// call code to generate exception
    //}
}
