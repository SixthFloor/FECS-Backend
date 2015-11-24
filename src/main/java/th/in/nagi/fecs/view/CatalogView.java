package th.in.nagi.fecs.view;

import th.in.nagi.fecs.view.BaseView.Standardized;

public class CatalogView {
	public interface Summary
			extends Personal, ProductDescriptionView.ElementalImage, SubCategoryView.Personal, CategoryView.Personal {
	}

	public interface Personal extends Standardized {
	}
	
	public interface Type extends Personal {
	}
	
	public interface ProductDescription extends Personal, ProductDescriptionView.Summary{
	}
}
