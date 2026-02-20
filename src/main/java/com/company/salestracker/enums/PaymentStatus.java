package com.company.salestracker.enums;

public enum PaymentStatus {
	
	SUCCESS {
		@Override
		public boolean canMoveTo(PaymentStatus next) {
			return false;
		}
	},
	FAILED {
		@Override
		public boolean canMoveTo(PaymentStatus next) {
			return false;
		}
	},
	PENDING {
		@Override
		public boolean canMoveTo(PaymentStatus next) {
			return next == SUCCESS || next == FAILED;
		}
	};

	public abstract boolean canMoveTo(PaymentStatus next);

}
