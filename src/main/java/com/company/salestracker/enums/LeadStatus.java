package com.company.salestracker.enums;

public enum LeadStatus {
	NEW {
		@Override
		public boolean canMoveTo(LeadStatus next) {
			return next == CONTACTED ;
		}
	},
	CONTACTED {
		@Override
		public boolean canMoveTo(LeadStatus next) {
			return next == LOST || next == QUALIFIED;
		}
	},
	QUALIFIED {
		@Override
		public boolean canMoveTo(LeadStatus next) {
			return false;
		}
	},
	LOST

	{
		@Override
		public boolean canMoveTo(LeadStatus next) {
			return false;
		}
	};

	public abstract boolean canMoveTo(LeadStatus next);

}
