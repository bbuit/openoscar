package org.oscarehr.common.dao;

import java.util.Hashtable;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.UserDSMessagePrefs;
import org.springframework.stereotype.Repository;

@Repository
public class UserDSMessagePrefsDao extends AbstractDao<UserDSMessagePrefs>{

	public UserDSMessagePrefsDao() {
		super(UserDSMessagePrefs.class);
	}

    public void saveProp(UserDSMessagePrefs prop) {
        this.persist(prop);
    }

    public void updateProp(UserDSMessagePrefs prop) {
        this.merge(prop);
    }

    public UserDSMessagePrefs getMessagePrefsOnType(String prov, String name) {
    	Query query = entityManager.createQuery("SELECT p FROM UserDSMessagePrefs p WHERE p.providerNo=? and p.resourceType=? and p.archived=true");
    	query.setParameter(1, prov);
    	query.setParameter(2, name);

        @SuppressWarnings("unchecked")
        List<UserDSMessagePrefs> list = query.getResultList();
        if( list != null && list.size() > 0 ) {
            UserDSMessagePrefs prop = list.get(0);
            return prop;
        }
        else
            return null;
    }



    public Hashtable<String,Long> getHashofMessages(String providerNo,String name){
    	Query query = entityManager.createQuery("SELECT p FROM UserDSMessagePrefs p WHERE p.providerNo=? and p.resourceType=? and p.archived=true");
    	query.setParameter(1, providerNo);
    	query.setParameter(2, name);

    	Hashtable<String,Long> retHash = new Hashtable<String,Long>();
        @SuppressWarnings("unchecked")
        List<UserDSMessagePrefs> list = query.getResultList();

        if( list != null && list.size() > 0 ) {
            for(UserDSMessagePrefs pref: list){
                retHash.put(pref.getResourceType()+pref.getResourceId(),pref.getResourceUpdatedDate().getTime());
            }
        }
        return retHash;
    }

    public UserDSMessagePrefs getDsMessage(String providerNo,String resourceType,String resourceId, boolean archived){
    	Query query = entityManager.createQuery("SELECT p FROM UserDSMessagePrefs p WHERE p.providerNo=? and p.resourceType=? and p.resourceId=? and p.archived = ? order by p.id DESC");
    	query.setParameter(1, providerNo);
    	query.setParameter(2, resourceType);
    	query.setParameter(3, resourceId);
    	query.setParameter(4,archived);

    	@SuppressWarnings("unchecked")
        List<UserDSMessagePrefs> list = query.getResultList();

        UserDSMessagePrefs pref=new UserDSMessagePrefs();
        if( list != null && list.size() > 0 ) {
            pref=list.get(0);
        }
        return pref;
    }
}