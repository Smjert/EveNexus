package nl.minicom.evenexus.persistence.dao;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.gson.Gson;

/**
 * Entity representing matching fulfilled buy and sell orders.
 * 
 * @author Michael
 * @author Lars
 */
@Entity
@Table(name = "transactionmatches")
public class TransactionMatch implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String KEY = "key";
	public static final String QUANTITY = "quantity";
	
	@Id
	private TransactionMatchIdentifier key;

	@Column(name = QUANTITY)
	private long quantity;

	public TransactionMatchIdentifier getKey() {
		return key;
	}

	public void setKey(TransactionMatchIdentifier key) {
		this.key = key;
	}

	/**
	 * Answer the number of items matching those transactions.
	 */
	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public long getBuyTransactionId() {
		return key.getBuyTransactionId();
	}
	
	public long getSellTransactionId() {
		return key.getSellTransactionId();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TransactionMatch) {
			TransactionMatch profit = (TransactionMatch) other;
			
			return new EqualsBuilder()
			.append(key, profit.key)
			.isEquals();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(key)
			.toHashCode();
	}
	
	public static void main(String[] args) {
		
		TransactionMatch match1 = new TransactionMatch();
		match1.setKey(new TransactionMatchIdentifier(1, 3));
		match1.setQuantity(500);
		
		System.err.println(new Gson().toJson(match1));
	}
	
}
