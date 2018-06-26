scDefine([
	"scbase/loader!dojo/_base/declare",
	
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!ias/utils/UIUtils",
	"scbase/loader!extn/customScreen/GiftWrap/GiftWrapPortletUI"
	
],
function(

	_dojodeclare,
	_scScreen,
	_iasUIUtils,
	_extnGiftWrapPortletUI
) {
    return _dojodeclare("extn.customScreen.GiftWrap.GiftWrapPortlet", [_extnGiftWrapPortletUI], {

	 

		orderSearchAction: function() {
			
			 var shipmentModel = null;
          
           _iasUIUtils.openWizardInEditor("extn.customScreen.search.extnShipmentSearchWizard",shipmentModel, "extn.editors.GiftWrapSearch.GiftWrapEditor", this, null);
		},
		
	});
});