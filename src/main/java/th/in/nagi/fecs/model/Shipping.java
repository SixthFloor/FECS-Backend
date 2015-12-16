package th.in.nagi.fecs.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import th.in.nagi.fecs.view.ShippingView;

/**
 * Order model
 * 
 * @author Chonnipa Kittisiriprasert
 *
 */
@Entity
@Table(name = "shipping")
public class Shipping {

	/**
	 * The possible status of Shipping.
	 */
	public static final int AVAILABLE = 0;
	public static final int RESERVED = 1;
	public static final int INPROGRESS = 2;
	public static final int DONE = 3;

	/**
	 * Shipping's id.
	 */
	@JsonView(ShippingView.Personal.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * The date that products send to User.
	 */
	@JsonView(ShippingView.Personal.class)
	private Date date;

	/**
	 * Shipping's status.
	 */
	@JsonView(ShippingView.Personal.class)
	private int status;
	
//	@ManyToOne
//	@JoinColumn(name = "address_id")
//	@Fetch(FetchMode.SELECT)
//	private Address address;

	@OneToOne(mappedBy = "shipping")
	private Order order;

	@JsonView(ShippingView.View.class)
	public User getUser() {
		if (order == null) {
			return null;
		}
		User user = order.getUser();
		return user;
	}

	@JsonView(ShippingView.View.class)
	public Integer getOrderNumber() {
		if (order == null) {
			return null;
		}
		Integer num = order.getOrderNumber();
		return num;
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public int getStatus() {
		return status;
	}

	public void resetId() {
		id = null;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
