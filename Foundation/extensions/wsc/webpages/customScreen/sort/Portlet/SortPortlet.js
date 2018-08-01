scDefine([
	"scbase/loader!dojo/_base/declare",
	
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!ias/utils/UIUtils",
	"scbase/loader!extn/customScreen/sort/Portlet/SortPortletUI"
	
],
function(

	_dojodeclare,
	_scScreen,
	_iasUIUtils,
	_extnSortPortletUI
) {
    return _dojodeclare("extn.customScreen.sort.Portlet.SortPortlet", [_extnSortPortletUI], {

	 

		orderSearchAction: function() {
			
			 var shipmentModel = null;
          
           _iasUIUtils.openWizardInEditor("extn.customScreen.search.extnShipmentSearchWizard",shipmentModel, "extn.editors.GiftWrapSearch.GiftWrapEditor", this, null);
		},
		
	});
});