scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/BaseTemplateUtils", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils", "scbase/loader!extn/mobile/picking/ShipmentPickDetailsUI", "scbase/loader!wsc/mobile/home/utils/MobileHomeUtils"], function(
_dojodeclare, _iasBaseTemplateUtils, _iasContextUtils, _iasUIUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _wscShipmentUtils, _extnShipmentPickDetailsUI, _wscMobileHomeUtils) {
    return _dojodeclare("extn.mobile.picking.ShipmentPickDetails", [_extnShipmentPickDetailsUI], {
        // custom code here
        initializeScreen: function(
        event, bEvent, ctrl, args) {
            var identifierMode = null;
            identifierMode = _scBaseUtils.getAttributeValue("identifierId", false, this);
            var shipmentModel = null;
            shipmentModel = _scScreenUtils.getModel(
            this, "Shipment");
            var userId = null;
            userId = _scModelUtils.getStringValueFromPath("Shipment.AssignedToUserId", shipmentModel);
            if (
            _scBaseUtils.isVoid(
            userId)) {
                _scWidgetUtils.hideWidget(
                this, "img_User", false);
                _scWidgetUtils.hideWidget(
                this, "lbl_AssociateName", false);
            }
            var screenType = null;
            screenType = _scModelUtils.getStringValueFromPath("Shipment.ScreenType", shipmentModel);
            if (
            _scBaseUtils.equals(
            identifierMode, "Pick")) {
                this.initializeScreenPick(
                shipmentModel);
            } else if (
            _scBaseUtils.equals(
            identifierMode, "Ship")) {
                this.initializeScreenShip(
                shipmentModel);
            } else if (
            _scBaseUtils.equals(
            identifierMode, "Pack")) {
                this.initializeScreenPack(
                shipmentModel);
            }
            var urlString = null;
            urlString = _scModelUtils.getStringValueFromPath("Shipment.ImageUrl", shipmentModel);
            if (!(
            _scBaseUtils.isVoid(
            urlString))) {
                var imageUrlModel = null;
                imageUrlModel = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
                _scModelUtils.setStringValueAtModelPath("CommonCode.CodeLongDescription", urlString, imageUrlModel);
                if(window.navigator.appVersion.indexOf("iPhone OS 8_1") > -1) {
					return;
				}else{
					_scScreenUtils.setModel(this, "clockImageBindingValues", imageUrlModel, null);
				}
            }
        },
        initializeScreenPick: function(
        shipmentModel) {
            var widgetUIdBean = null;
            widgetUIdBean = {};
            widgetUIdBean["HoldLocationWidgetUId"] = "lnk_AssignToHoldAction";
            widgetUIdBean["DueInLabelWidgetUId"] = "lbl_timeRemaining";
            widgetUIdBean["DueInIconWidgetUId"] = "img_TimeRmnClock";
			widgetUIdBean["RecordCustomerPickupWidgetUId"] = "lnk_RecordCustomerPickupAction"
            _wscMobileHomeUtils.showNextTask(
            this, shipmentModel, "Pick", "lnk_PickAction", "lbl_Status", widgetUIdBean);
        },
        initializeScreenShip: function(
        shipmentModel) {
            var widgetUIdBean = null;
            widgetUIdBean = {};
            widgetUIdBean["PackWidgetUId"] = "lnk_PackAction";
            widgetUIdBean["DueInLabelWidgetUId"] = "lbl_timeRemaining";
            widgetUIdBean["DueInIconWidgetUId"] = "img_TimeRmnClock";
            _wscMobileHomeUtils.showNextTask(
            this, shipmentModel, "Ship", "lnk_ShipAction", "lbl_Status", widgetUIdBean);
        },
        initializeScreenPack: function(
        shipmentModel) {
            var widgetUIdBean = null;
            widgetUIdBean = {};
            widgetUIdBean["ShipWidgetUId"] = "lnk_ShipAction";
            widgetUIdBean["DueInLabelWidgetUId"] = "lbl_timeRemaining";
            widgetUIdBean["DueInIconWidgetUId"] = "img_TimeRmnClock";
            _wscMobileHomeUtils.showNextTask(
            this, shipmentModel, "Pack", "lnk_PackAction", "lbl_Status", widgetUIdBean);
        },
        getCustomerName: function(
        dataValue, screen, widget, nameSpace, shipmentModel) {
            var custName = null;
            custName = _wscMobileHomeUtils.buildNameFromShipment(
            this, shipmentModel);
            return custName;
        },
        hideCarrierIfEmpty: function(
        dataValue, screen, widget, nameSpace, shipmentModel) {
            var custName = null;
            if (
            _scBaseUtils.isVoid(
            dataValue)) {
                _scWidgetUtils.hideWidget(
                this, "lbl_Carrier", true);
            } else {
                return dataValue;
            }
        },
        showHideHoldLocation: function(
        dataValue, screen, widget, nameSpace, shipmentModel) {
            _wscShipmentUtils.showHideHoldLocation(
            this, shipmentModel, widget);
            return dataValue;
        },
        getNumberOfProductsLabel: function(
        dataValue, screen, widget, nameSpace, shipmentModel) {
            var returnValue = " ";
            if (
            _scBaseUtils.isVoid(
            dataValue)) {
                return returnValue;
            } else if (
            _scBaseUtils.equals(
            dataValue, "1")) {
                returnValue = _scScreenUtils.getString(
                this, "Label_OneProduct");
            } else {
                var inputArray = null;
                inputArray = [];
                inputArray.push(
                dataValue);
                returnValue = _scScreenUtils.getFormattedString(
                this, "Label_ProductCount", inputArray);
            }
            return returnValue;
        },
        assignToHoldDefaultAction: function(
        event, bEvent, ctrl, args) {
            var targetModel = null;
            shipmentDetails = _scBaseUtils.getTargetModel(
            this, "common_getShipmentDetails_input", null);
            _iasUIUtils.openWizardInEditor("wsc.components.shipment.backroompick.HoldLocationWizard", shipmentDetails, "wsc.desktop.editors.ShipmentEditor", this, false);
        },
		recordCustomerPickupAction: function(
        event, bEvent, ctrl, args) {
            var targetModel = null;
            shipmentDetails = _scBaseUtils.getTargetModel(
            this, "common_getShipmentDetails_input", null);
            _iasUIUtils.openWizardInEditor("wsc.components.shipment.customerpickup.CustomerPickUpWizard", shipmentDetails, "wsc.desktop.editors.ShipmentEditor", this, false);
        },
        pickDefaultAction: function(
        event, bEvent, ctrl, args) {
            var shipmentModel = null;
            shipmentModel = _scScreenUtils.getTargetModel(
            this, "common_getShipmentDetails_input", null);
            _wscShipmentUtils.openBackroomPickWizard(
            this, shipmentModel);
        },
        shipDefaultAction: function(
        event, bEvent, ctrl, args) {
            var shipmentModel = null;
            shipmentModel = _scScreenUtils.getTargetModel(
            this, "common_getShipmentDetails_input", null);
            _wscShipmentUtils.openBackroomPickWizard(
            this, shipmentModel);
        },
        handleMashupCompletion: function(
        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
            _iasBaseTemplateUtils.handleMashupCompletion(
            mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },
        packDefaultAction: function(
        event, bEvent, ctrl, args) {
            var targetModel = null;
            targetModel = _scBaseUtils.getTargetModel(
            this, "common_getShipmentDetails_input", null);
            _wscShipmentUtils.openPackingWizard(
            this, targetModel);
        },
        openPickDetails: function(
        event, bEvent, ctrl, args) {
            var shipmentSummaryTargetModel = null;
            shipmentSummaryTargetModel = _scBaseUtils.getTargetModel(
            this, "shipmentSummaryWizard_input", null);
            var editorName = "wsc.mobile.editors.MobileEditor";
            _iasUIUtils.openWizardInEditor("wsc.components.shipment.summary.ShipmentSummaryWizard", shipmentSummaryTargetModel, editorName, this);
        },
        toggleShipNodeMsg: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            var currentStoreInContext = null;
            var formattedTxt = null;
            currentStoreInContext = _iasContextUtils.getFromContext("CurrentStore");
            if (
            _scBaseUtils.equals(
            dataValue, currentStoreInContext)) {
                if (
                _scWidgetUtils.isWidgetVisible(
                this, "shipNodeMsg")) {
                    _scWidgetUtils.hideWidget(
                    this, "storeMsgPnl", true);
                }
            } else {
                _scWidgetUtils.showWidget(
                this, "storeMsgPnl", true, null);
            }
            formattedTxt = _scScreenUtils.getString(
            this, "DifferentShipNodeMessage");
            return formattedTxt;
        }
    });
});