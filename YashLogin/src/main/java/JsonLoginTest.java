package main.java;

import java.util.Hashtable;

import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

@WebService
@Path("/loginJ")
public class JsonLoginTest {
	
	
	public JsonLoginTest() {
		// TODO Auto-generated constructor stub
		System.out.println("JsonLoginTest");
	}
	
	 @Path("/authenticate")
	 @POST
	 @Consumes(MediaType.APPLICATION_JSON)
	 public JSONObject authenticate(LoginForm form)throws JSONException{
		 
		 final  String ldapAdServer = ApplicationConstant.LDAP_SERVER;
	        final String ldapSearchBase =ApplicationConstant.LDADP_SEARCH_BASE;
	        final String ldapUsername = form.getUserName();
	        final String ldapPassword = form.getPassword();
	        String[] lookUpUser = ldapUsername.split("@");
	        final String ldapAccountToLookup = lookUpUser[0];	        
	        Hashtable<String, Object> env = new    Hashtable<String, Object>();
	        JSONObject jsonObject = new JSONObject();
	        Attributes attribute;
	       
	        env.put(Context.SECURITY_AUTHENTICATION, "simple");
	        if(ldapUsername != null) {
	            env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
	        }
	        if(ldapPassword != null) {
	            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
	        }
	        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	        env.put(Context.PROVIDER_URL, ldapAdServer);
	        //ensures that objectSID attribute values
	        //will be returned as a byte[] instead of a String
	        env.put("java.naming.ldap.attributes.binary", "objectSID");
	        env.put(Context.REFERRAL,"follow");   
	        // the following is helpful in debugging errors
	        //env.put("com.sun.jndi.ldap.trace.ber", System.err);
	       
	        try {
				DirContext ctx = new InitialDirContext(env);
	            System.out.println("Ldap Server connection done");
				//1) lookup the ldap account
				SearchResult srLdapUser = findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
				JSONObject jsObject = new JSONObject();
				attribute  = srLdapUser.getAttributes();
		        jsonObject = printAttrs(attribute,jsObject);
		       
				//2) get the SID of the users primary group
			    // String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
			    //3) get the users Primary Group
			    // String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
		 
		 return jsonObject;
		 
	 }
	 
	 public SearchResult findAccountByAccountName(DirContext ctx, String ldapSearchBase, String accountName) throws NamingException {	       
			
 		String searchFilter = "(&(objectClass=user)(sAMAccountName=" +accountName+"))";
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);         
			SearchResult searchResult = null;
			 try {
			if(results.hasMoreElements()) {
			     searchResult = (SearchResult) results.nextElement();
			    //make sure there is not another item available, there should be only 1 match
			    if(results.hasMoreElements()) {
			        System.err.println("Matched multiple users for the accountName: " + accountName);
			        return null;
			    }
			}
							
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(results.toString());
			ctx.close();
			e.printStackTrace();
			
		}finally{
			ctx.close();
		}
			 return searchResult;
 }
 
 static JSONObject printAttrs(Attributes attrs,JSONObject jsonObject)throws JSONException {
     if (attrs == null) {
       System.out.println("No attributes");
     } else {
      // / Print each attribute /
       try {
         for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
           Attribute attr = (Attribute) ae.next();
           System.out.println("attribute: " + attr.getID());
           System.out.println("value: " + ae.next());
           jsonObject.putOnce(attr.getID(),attr);
           
        /* //  / print each value /
           for (NamingEnumeration e = attr.getAll(); e.hasMore(); 
           System.out.println("value: " + e.next()));*/
         }
       } catch (NamingException e) {
         e.printStackTrace();
       }
     }
		return jsonObject;
   }
	
	

}
