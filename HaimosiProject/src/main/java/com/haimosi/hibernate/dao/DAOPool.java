package com.haimosi.hibernate.dao;

import logia.utility.pool.ObjectPool;

import com.haimosi.define.Config;

/**
 * The Class DAOPool.
 * 
 * @author Paul Mai
 */
public class DAOPool {

	/** The credit pool. */
	public static ObjectPool<CreditAccountDAO> creditPool      = new ObjectPool<CreditAccountDAO>(Config.num_core_dao_in_pool,
			Config.num_max_dao_in_pool, Config.interval_validate_pool) {

		@Override
		protected CreditAccountDAO createObject() {
			return new CreditAccountDAO();
		}
	};

	/** The item pool. */
	public static ObjectPool<ItemDAO>          itemPool        = new ObjectPool<ItemDAO>(Config.num_core_dao_in_pool, Config.num_max_dao_in_pool,
			Config.interval_validate_pool) {

		@Override
		protected ItemDAO createObject() {
			return new ItemDAO();
		}
	};

	/** The light pool. */
	public static ObjectPool<LightDAO>         lightPool       = new ObjectPool<LightDAO>(Config.num_core_dao_in_pool, Config.num_max_dao_in_pool,
			Config.interval_validate_pool) {

		@Override
		protected LightDAO createObject() {
			return new LightDAO();
		}
	};
	/** The role pool. */
	public static ObjectPool<RoleDAO>          rolePool        = new ObjectPool<RoleDAO>(Config.num_core_dao_in_pool, Config.num_max_dao_in_pool,
			Config.interval_validate_pool) {

		@Override
		protected RoleDAO createObject() {
			return new RoleDAO();
		}
	};

	/** The scale pool. */
	public static ObjectPool<ScaleDAO>         scalePool       = new ObjectPool<ScaleDAO>(Config.num_core_dao_in_pool, Config.num_max_dao_in_pool,
			Config.interval_validate_pool) {

		@Override
		protected ScaleDAO createObject() {
			return new ScaleDAO();
		}
	};

	/** The transaction pool. */
	public static ObjectPool<TransactionDAO>   transactionPool = new ObjectPool<TransactionDAO>(Config.num_core_dao_in_pool,
			Config.num_max_dao_in_pool, Config.interval_validate_pool) {

		@Override
		protected TransactionDAO createObject() {
			return new TransactionDAO();
		}
	};

	/** The user pool. */
	public static ObjectPool<UserDAO>          userPool        = new ObjectPool<UserDAO>(Config.num_core_dao_in_pool, Config.num_max_dao_in_pool,
			Config.interval_validate_pool) {

		@Override
		protected UserDAO createObject() {
			return new UserDAO();
		}
	};

	/**
	 * Release.
	 */
	public static void release() {
		DAOPool.creditPool.shutdown();
		DAOPool.lightPool.shutdown();
		DAOPool.itemPool.shutdown();
		DAOPool.rolePool.shutdown();
		DAOPool.scalePool.shutdown();
		DAOPool.transactionPool.shutdown();
		DAOPool.userPool.shutdown();
	}
}
