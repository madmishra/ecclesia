scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/BaseTemplateUtils", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/PrintUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!wsc/components/common/utils/CommonUtils", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils", "scbase/loader!extn/customScreen/search/extnShipmentDetailsUI", "scbase/loader!extn/customScreen/search/utils/SearchUtils","scbase/loader!sc/plat/dojo/utils/EditorUtils"], function(
_dojodeclare, _iasBaseTemplateUtils, _iasContextUtils, _iasPrintUtils, _iasUIUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _wscCommonUtils, _wscShipmentUtils, _extnextnShipmentDetailsUI, _extnSearchUtils,_scEditorUtils) {
    return _dojodeclare("extn.customScreen.search.extnShipmentDetails", [_extnextnShipmentDetailsUI], {
        // custom code here
        // handle_searchPickTicket: function(
        // mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
        //     _iasPrintUtils.printHtmlOutput(
        //     modelOutput);
        // },
        handle_changeShipmentToUpdateQty: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            _wscShipmentUtils.openBackroomPickWizard(
            this, modelOutput);
        },
        // handle_getShipmentDetailsForRecordCustomerPick: function(
        // mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
        //     _wscShipmentUtils.handleValidationOutput(
        //     this, mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        // },
        // handle_containerPack_changeShipment: function(
        // mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
        //     _wscShipmentUtils.openPackingWizard(
        //     this, modelOutput);
        // },
        // handle_getShipmentDetailsForPack: function(
        // mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
        //     _wscShipmentUtils.handleValidationOutput(
        //     this, mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        // },
        // handle_getShipmentDetailsForBackroomPick: function(
        // mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
        //     _wscShipmentUtils.handleValidationOutput(
        //     this, mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel);
        // },
        initializeScreen: function(
        event, bEvent, ctrl, args) {
            var shipmentList = null;
            var status = null;
            var deliveryMethod = null;
            var shipNode = null;
            shipmentList = _scScreenUtils.getModel(
            this, "getShipmentList_output");
            _wscShipmentUtils.showNextTask(
            this, shipmentList, "Shipment.ShipNode", "lnkStartBRP", "lnkContinueBRP", "lnkPrint", "lnkPack", "lnkContinuePack", "lnkStartCustomerPickup");
            _wscShipmentUtils.showHideHoldLocation(
            this, shipmentList, "lblHoldLocation");
            deliveryMethod = _scModelUtils.getStringValueFromPath("Shipment.DeliveryMethod", shipmentList);
            if (
            _scBaseUtils.equals(
            deliveryMethod, "SHP")) {
                _scWidgetUtils.hideWidget(
                this, "lblExpectedShipDate", true);
                _scWidgetUtils.showWidget(
                this, "lblCarrier", true, null);
            }
            _extnSearchUtils.scrollCenter();
            _scWidgetUtils.setFocusOnWidgetUsingUid(
            this, "lblOrderNo");
            status = _scModelUtils.getStringValueFromPath("Shipment.Status.Status", shipmentList);
            if (!(
            _wscShipmentUtils.showSLA(
            status))) {
                _scWidgetUtils.hideWidget(
                this, "lbl_timeRemaining", true);
                _scWidgetUtils.hideWidget(
                this, "img_TimeRmnClock", true);
            } else {
                var urlString = null;
                urlString = _scModelUtils.getStringValueFromPath("Shipment.ImageUrl", shipmentList);
                if (!(
                _scBaseUtils.isVoid(
                urlString))) {
                    var imageUrlModel = null;
                    imageUrlModel = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
                    _scModelUtils.setStringValueAtModelPath("CommonCode.CodeLongDescription", urlString, imageUrlModel);
                    _scModelUtils.setStringValueAtModelPath("CommonCode.CodeShortDescription", _scModelUtils.getStringValueFromPath("Shipment.ImageAltText", shipmentList), imageUrlModel);
                    _scScreenUtils.setModel(
                    this, "clockImageBindingValues", imageUrlModel, null);
                }
            }
        },
        // handlePickTicket: function(
        // event, bEvent, ctrl, args) {
        //     var targetModel = null;
        //     var shipments = null;
        //     targetModel = _scScreenUtils.getTargetModel(
        //     this, "getShipmentDetails_input", null);
        //     shipments = _scModelUtils.createNewModelObjectWithRootKey("Shipments");
        //     _scModelUtils.setStringValueAtModelPath("Shipments.Shipment.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", targetModel), shipments);
        //     _iasUIUtils.callApi(
        //     this, shipments, "searchPickTicket", null);
        // },
        // getEmailID: function(
        // dataValue, screen, widget, namespace, modelObj, options) {
        //     if (
        //     _scBaseUtils.isVoid(
        //     dataValue)) {
        //         _scWidgetUtils.hideWidget(
        //         this, "pnlEmailHolder", true);
        //     }
        //     return dataValue;
        // },
        // getDayPhone: function(
        // dataValue, screen, widget, namespace, modelObj, options) {
        //     if (
        //     _scBaseUtils.isVoid(
        //     dataValue)) {
        //         _scWidgetUtils.hideWidget(
        //         this, "pnlPhoneHolder", true);
        //     }
        //     return dataValue;
        // },
        formatOrderNo: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            var formattedOrderNo = null;
            formattedOrderNo = _wscCommonUtils.getDisplayOrderNumber(
            dataValue, "COMMAS");
            return formattedOrderNo;
        },
        // toggleShipNodeMsg: function(
        // dataValue, screen, widget, namespace, modelObj, options) {
        //     var currentStoreInContext = null;
        //     var formattedTxt = null;
        //     currentStoreInContext = _iasContextUtils.getFromContext("CurrentStore");
        //     if (
        //     _scBaseUtils.equals(
        //     dataValue, currentStoreInContext)) {
        //         if (
        //         _scWidgetUtils.isWidgetVisible(
        //         this, "shipNodeMsg")) {
        //             _scWidgetUtils.hideWidget(
        //             this, "storeMsgPnl", true);
        //         }
        //     } else {
        //         _scWidgetUtils.showWidget(
        //         this, "storeMsgPnl", true, null);
        //     }
        //     formattedTxt = _scScreenUtils.getString(
        //     this, "DifferentShipNodeMessage");
        //     return formattedTxt;
        // },
        // getFormattedNameDisplay: function(
        // dataValue, screen, widget, namespace, modelObj, options) {
        //     var billToAddressModel = null;
        //     billToAddressModel = _scModelUtils.getModelObjectFromPath("Shipment.BillToAddress", modelObj);
        //     if (!(
        //     _scBaseUtils.isVoid(
        //     billToAddressModel))) {
        //         dataValue = _wscShipmentUtils.getNameDisplay(
        //         this, billToAddressModel);
        //     }
        //     return dataValue;
        // },
        openShipmentDetails: function(
        event, bEvent, ctrl, args) {
            var targetModel = null;
            targetModel = _scScreenUtils.getTargetModel(
            this, "getShipmentDetails_input", null);
            _iasUIUtils.openWizardInEditor("extn.wizards.BackroomPickupWizard", targetModel, "wsc.desktop.editors.ShipmentEditor", this, null);
			var editor = "extn.editors.MyOrderEditor1";
        },
        // getShipmentDetailsForPack: function() {
        //     var shipmentInputModel = null;
        //     shipmentInputModel = _scScreenUtils.getModel(
        //     this, "getShipmentList_output");
        //     _wscShipmentUtils.handleStartPack_OnClick(
        //     this, shipmentInputModel, "getShipmentDetailsForPack");
        // },
        // getShipmentDetailsForRecordPick: function() {
        //     var shipmentInputModel = null;
        //     shipmentInputModel = _scScreenUtils.getModel(
        //     this, "getShipmentList_output");
        //     _wscShipmentUtils.handleBackroomPick_OnClick(
        //     this, shipmentInputModel, "getShipmentDetailsForBackroomPick");
        // },
        // getShipmentDetailsForRecordCustomerPick: function() {
        //     var shipmentInputModel = null;
        //     shipmentInputModel = _scScreenUtils.getModel(
        //     this, "getShipmentList_output");
        //     _wscShipmentUtils.handleCustomerPick_OnClick(
        //     this, shipmentInputModel, "getShipmentDetailsForRecordCustomerPick");
        // },
        // validateShipmentBeforeRecordCustomerPick: function(
        // modelOutput) {
        //     var errorElement = null;
        //     var action = null;
        //     var errorDescription = null;
        //     errorElement = _scModelUtils.getModelObjectFromPath("Shipment.Error", modelOutput);
        //     if (!(
        //     _scBaseUtils.isVoid(
        //     errorElement))) {
        //         errorDescription = _scModelUtils.getStringValueFromPath("ErrorDescription", errorElement);
        //         _iasBaseTemplateUtils.showMessage(
        //         this, errorDescription, "error", null);
        //     } else {
        //         _iasUIUtils.openWizardInEditor("extn.customScreen.customerpickup.CustomerPickUpWizard", modelOutput, "wsc.desktop.editors.ShipmentEditor", this, null);
        //     }
        // },
        // startBackroomPick: function(
        // event, bEvent, ctrl, args) {
        //     this.openBackroomPickWizard();
        // },
        // openBackroomPickWizard: function() {
        //     var shipmentModel = null;
        //     shipmentModel = _scScreenUtils.getTargetModel(
        //     this, "getShipmentDetails_input", null);
        //     _wscShipmentUtils.openBackroomPickWizard(
        //     this, shipmentModel);
        // },
        // openPackWizard: function() {
        //     var shipmentModel = null;
        //     shipmentModel = _scScreenUtils.getTargetModel(
        //     this, "getShipmentDetails_input", null);
        //     _wscShipmentUtils.openPackingWizard(
        //     this, shipmentModel);
        // },
        // continueBackroomPick: function(
        // event, bEvent, ctrl, args) {
        //     this.openBackroomPickWizard();
        // },
        // startPack: function(
        // event, bEvent, ctrl, args) {
        //     this.openPackWizard();
        // },
        // continuePack: function(
        // event, bEvent, ctrl, args) {
        //     this.openPackWizard();
        // },
        // abandonedCallbackHandler: function(
        // actionPerformed, outputModel, popupParams) {
        //     _wscShipmentUtils.handleAssignedUserPopupResponse(
        //     this, actionPerformed, outputModel, popupParams, "containerPack_unpackShipment", "containerPack_changeShipment", "containerPack_changeShipmentStatus", "changeShipmentToUpdateQty");
        // },
        // startCustomerPick: function(
        // event, bEvent, ctrl, args) {
        //     this.getShipmentDetailsForRecordCustomerPick();
        // }
    });
});