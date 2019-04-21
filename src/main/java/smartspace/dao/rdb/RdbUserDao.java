package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.UserDao;
import smartspace.data.UserEntity;

@Repository
public class RdbUserDao implements UserDao<String>{

	private UserCrud userCrud;
	private GenericIdGeneratorCrud genericIdGeneratorCrud;	
	
	@Autowired
	public RdbUserDao(UserCrud userCrud, GenericIdGeneratorCrud genericIdGeneratorCrud) {		
		super();
		this.genericIdGeneratorCrud = genericIdGeneratorCrud;
		this.userCrud = userCrud;
	}
	
	public RdbUserDao() {		
	
	}
	
	
	@Override
	@Transactional
	public UserEntity create(UserEntity userEntity) {
		//SQL: INSERT INTO USER_ENTITY_TABLE (USER_ID, USER_SMARTSPACE) VALUES (?,?);
		//create and enter id into db
		GenericIdGenerator nextId = 
				this.genericIdGeneratorCrud.save(new GenericIdGenerator());
		
		//set user key and destroy the row in db
		userEntity.setKey(nextId.getId() + "=" + userEntity.getUserSmartspace());
		this.genericIdGeneratorCrud.delete(nextId);
		
		//if user doesn't exists then add it, else throw RuntimeException
		if(!this.userCrud.existsById(userEntity.getKey())) {
			UserEntity rv = this.userCrud.save(userEntity);
			return rv;
		}
		else {
			throw new RuntimeException("user entity already exist with key:" + userEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<UserEntity> readById(String userKey) {
		//SQL: SELECT
		return this.userCrud.findById(userKey);
	}

	@Override
	@Transactional(readOnly=true)
	public List<UserEntity> readAll() {
		//SQL: SELECT
		List<UserEntity> rv = new ArrayList<>();
		this.userCrud.findAll().forEach(rv::add);
		return rv;
	}

	@Override
	@Transactional
	public void update(UserEntity userEntity) {
		
		UserEntity existing = this.readById(userEntity.getKey())
				.orElseThrow(() -> new RuntimeException("not user to update"));
		
		if(userEntity.getAvatar() != null) {
			existing.setAvatar(userEntity.getAvatar());
		}
		if(userEntity.getRole() != null) {
			existing.setRole(userEntity.getRole());
		}
		if(userEntity.getUserEmail() != null) {
			existing.setUserEmail(userEntity.getUserEmail());
		}
		if(userEntity.getUserName() != null) {
			existing.setUserName(userEntity.getUserName());
		}
		if(userEntity.getUserSmartspace() != null) {
			existing.setUserSmartspace(userEntity.getUserSmartspace());
		}
		// SQL: UPDATE
		this.userCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteAll() {
		// SQL: DELETE
		this.userCrud.deleteAll();
	}
	
//	@Override
//	@Transactional(readOnly=true)
//	public List<UserEntity> readMessageWithNameContaining(
//			String text, 
//			int size, 
//			int page) {
//		
//		return this.userCrud
//				.findAllByNameLike(
//						"%" + text + "%",
//						PageRequest.of(page, size));
//	}

}