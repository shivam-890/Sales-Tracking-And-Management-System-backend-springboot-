package com.company.salestracker.enums;

public enum DealStatus {
	
	
	NEGOTIATION {
		@Override
		public boolean canMoveTo(DealStatus next) {
			return next == PROPOSAL ;
		}
	},
	PROPOSAL {
		@Override
		public boolean canMoveTo(DealStatus next) {
			return next == CLOSED_WON || next == CLOSED_LOST;
		}
	},
	CLOSED_WON {
		@Override
		public boolean canMoveTo(DealStatus next) {
			return false;
		}
	},
	CLOSED_LOST
	{
		@Override
		public boolean canMoveTo(DealStatus next) {
			return false;
		}
	};

	public abstract boolean canMoveTo(DealStatus next);

}
