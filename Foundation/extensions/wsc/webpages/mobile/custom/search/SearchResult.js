scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/mobile/custom/search/SearchResultUI", "scbase/loader!wsc/mobile/home/utils/MobileHomeUtils"], function(
_dojodeclare, _iasContextUtils, _iasRepeatingScreenUtils, _iasScreenUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _extnSearchResultUI, _wscMobileHomeUtils) {
    return _dojodeclare("extn.mobile.custom.search.SearchResult", [_extnSearchResultUI], {
        // custom code here
        initializeScreen: function(
        event, bEvent, ctrl, args) {
            var shipmentModel = null;
            shipmentModel = _scScreenUtils.getModel(
            this, "getShipmentList_output");
        },
        returnToOrderSearch: function(
        event, bEvent, ctrl, args) {
            _wscMobileHomeUtils.openScreenWithInputData("wsc.mobile.home.search.Search", _iasContextUtils.getFromContext("SearchCriteria"), "wsc.mobile.editors.MobileEditor");
        },
        getIdentifierRepeatingScreenData: function(
        shipmentModel, screen, widget, namespace, modelObject) {
            var repeatingScreenId = "wsc.mobile.common.screens.shipment.picking.ShipmentPickDetails";
            var returnValue = null;
            var identifierId = "Ship";
            var shipmentStatus = null;
            var deliveryMethod = null;
            shipmentStatus = _scModelUtils.getStringValueFromPath("Status.Status", shipmentModel);
            deliveryMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod", shipmentModel);
           
            var additionalParamsBean = null;
            additionalParamsBean = {};
            additionalParamsBean["identifierId"] = identifierId;
            var namespaceMapBean = null;
            namespaceMapBean = {};
            namespaceMapBean["parentnamespace"] = "getShipmentList_output";
            namespaceMapBean["childnamespace"] = "Shipment";
            namespaceMapBean["parentpath"] = "Shipments";
            namespaceMapBean["childpath"] = "Shipment";
            returnValue = _iasScreenUtils.getRepeatingScreenData(
            repeatingScreenId, namespaceMapBean, additionalParamsBean);
            return returnValue;
        }
    });
});