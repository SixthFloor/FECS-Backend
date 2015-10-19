package th.in.nagi.fecs.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import th.in.nagi.fecs.model.Product;
import th.in.nagi.fecs.model.User;

/**
 * Implemented user repository
 * 
 * @author Chonnipa Kittisiriprasert
 *
 */
@Repository("userRepository")
public class UserRepositoryImpl extends AbstractRepository<User, Integer>implements UserRepository {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAll() {
		Criteria criteria = createEntityCriteria();
		return (List<User>) criteria.list();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByKey(Integer key) {
		return getByKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(User user) {
		persist(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Integer key) {
		remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByEmail(String email) {
		Criteria criteria = createEntityCriteria();
		criteria.add(Restrictions.eq("email", email));
		return (User) criteria.uniqueResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeByEmail(String email) {
		remove(findByEmail(email));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findAndAscByFirstName(int start, int size) {
		List<User> list = createEntityCriteria().addOrder(org.hibernate.criterion.Order.asc("firstName"))
				.setFirstResult(start).setMaxResults(size).list();
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findAndDescByFirstName(int start, int size) {
		List<User> list = createEntityCriteria().addOrder(org.hibernate.criterion.Order.desc("firstName"))
				.setFirstResult(start).setMaxResults(size).list();
		return list;
	}

}
