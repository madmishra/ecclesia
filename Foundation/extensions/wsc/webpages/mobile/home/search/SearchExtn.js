
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/dom","scbase/loader!extn/mobile/home/search/SearchExtnUI","scbase/loader!wsc/mobile/home/utils/MobileHomeUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!ias/utils/ContextUtils"]
,
function(			 
			    _dojodeclare, _dojodom
			 ,
				_extnSearchExtnUI
				,
				_wscMobileHomeUtils
				,
				_scBaseUtils,
				_scModelUtils,
				_iasContextUtils
){ 
	return _dojodeclare("extn.mobile.home.search.SearchExtn", [_extnSearchExtnUI],{
	// custom code here
	orderSearch: function(event, bEvent, ctrl, args) {
            
		if (
            this.isSearchCriteriaValid()) {
                var shipmentSearchCriteriaModel = null;
                var includeOtherStoresModel = null;
                shipmentSearchCriteriaModel = _scBaseUtils.getTargetModel(
                this, "getShipmentSearch_input", null);
                if (
                _scBaseUtils.isVoid(
                shipmentSearchCriteriaModel)) {
                    shipmentSearchCriteriaModel = _scModelUtils.createModelObjectFromKey("Shipment", shipmentSearchCriteriaModel);
                }
                includeOtherStoresModel = _scBaseUtils.getTargetModel(
                this, "IncludeOrdersPickedInOtherStore", null);
                if (
                _scBaseUtils.equals(
                _scModelUtils.getStringValueFromPath("isChecked", includeOtherStoresModel), "Y")) {
                    _scModelUtils.setStringValueAtModelPath("Shipment.ShipNode", " ", shipmentSearchCriteriaModel);
                } else {
                    _scModelUtils.setStringValueAtModelPath("Shipment.ShipNode", _iasContextUtils.getFromContext("CurrentStore"), shipmentSearchCriteriaModel);
                }
                _scModelUtils.setStringValueAtModelPath("Shipment.OrderByAttribute", this.orderByAttribute, shipmentSearchCriteriaModel);
                _scBaseUtils.removeBlankAttributes(
                shipmentSearchCriteriaModel);
                _iasContextUtils.addToContext("SearchCriteria", shipmentSearchCriteriaModel);
                _wscMobileHomeUtils.openScreenWithInputData("extn.mobile.custom.search.SearchResult", shipmentSearchCriteriaModel, "wsc.mobile.editors.MobileEditor");
            } 
        }
			});
});

