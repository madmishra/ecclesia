scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/EventUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!wsc/components/product/common/utils/ProductUtils", "scbase/loader!wsc/components/shipment/backroompick/utils/BackroomPickUpUtils", "scbase/loader!extn/customScreen/Line/ShipmentLineDetailsUI", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils"], function (
    _dojodeclare, _iasEventUtils, _iasScreenUtils, _iasUIUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _wscProductUtils, _wscBackroomPickUpUtils, _extnShipmentLineDetailsUI, _wscShipmentUtils, _scResourcePermissionUtils, _scWizardUtils, _scEditorUtils) {
    return _dojodeclare("extn.customScreen.Line.ShipmentLineDetails", [_extnShipmentLineDetailsUI], {
        // custom code here
        initializeScreen: function (
            event, bEvent, ctrl, args) {
            var shipmentLineModel = null;
            var shipmentLinePickedQuantity = null;
            var varItemWidget = null;
            shipmentLineModel = _scScreenUtils.getModel(
                this, "ShipmentLine");
            var quantityTextBoxModel = null;
            quantityTextBoxModel = {};
            shipmentLinePickedQuantity = _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickedQuantity", shipmentLineModel);
			
			this.updateItemDetails();

            // if (_iasUIUtils.isValueNumber(shipmentLinePickedQuantity)) {
            //     this.pickedQuantity = shipmentLinePickedQuantity;
            // } else {
            //     this.pickedQuantity = _scBaseUtils.formatNumber("0", "ShipmentLine.BackroomPickedQuantity", "Quantity");
            // }

            // _scModelUtils.setStringValueAtModelPath("Quantity", this.pickedQuantity, quantityTextBoxModel);
            // _scScreenUtils.setModel(
            //     this, "QuantityTextBoxModel_Input", quantityTextBoxModel, null);
            // _scScreenUtils.setModel(
            //     this, "QuantityReadOnlyModel_Input", quantityTextBoxModel, null);
            // varItemWidget = _wscProductUtils.createItemVariationPanel(
            //     this, "itemVariationPanelUp", shipmentLineModel, "ShipmentLine.OrderLine.ItemDetails.AttributeList.Attribute");
            // if (!(
            //     _scBaseUtils.isVoid(
            //         varItemWidget))) {
            //     _scWidgetUtils.placeAt(
            //         this, "itemVariationPanelHolder", "itemVariationPanelUp", null);
            // }
            // if (
            //     _scBaseUtils.equals(
            //         this.flowName, "ContainerPack")) {
            //     var bundleString = null;
            //     bundleString = _scScreenUtils.getString(
            //         this, "Label_Packed");
            //     _scWidgetUtils.setValue(
            //         this, "pickedLabel", bundleString, false);
            // }

            // if (!_scResourcePermissionUtils.hasPermission("WSC000029")) {
            //     _scWidgetUtils.showWidget(this, "bpickedQtyLbl", true, null);
            //     _scWidgetUtils.hideWidget(this, "uom_lbl", false);
            //     var quantityReadOnlyModel = {};
            //     _scModelUtils.setStringValueAtModelPath("Quantity", this.pickedQuantity, quantityReadOnlyModel);
            //     _scScreenUtils.setModel(this, "QuantityReadOnlyModel_Input", quantityReadOnlyModel, null);
            // }
            // var wizardModel = null;
            // _iasUIUtils.setWizardModel(this, "shipmentKeyForWizard", wizardModel, null);

            // var shortageQty = 0;
            // shortageQty = _scModelUtils.getNumberValueFromPath("ShipmentLine.ShortageQty", shipmentLineModel);
            // if (
            // shortageQty >= 1) {
            //     this.handleShipmentLineShortage(
            //     shipmentLineModel);
            // } else {
            //     _wscBackroomPickUpUtils.validatePickedQuantity(
            //     this, shipmentLineModel, "productScanImagePanel", "completelyPickedLabel", "productScanShortageImagePanel", "updateShortageResolutionImage", "shortageResolutionLink");
            // }
        },
			updateAllValues: function (modelObject) {
				var arrayListObject = modelObject.ItemList.Item[0];
				var MCAT = "";
				var LM = ""
				var arrayList = arrayListObject.AdditionalAttributeList.AdditionalAttribute;
				if (arrayListObject.ClassificationCodes && arrayListObject.ClassificationCodes.CommodityCode)
					MCAT = arrayListObject.ClassificationCodes.CommodityCode;
				if (arrayListObject.PrimaryInformation && arrayListObject.PrimaryInformation.ProductLine)
					LM = arrayListObject.ClassificationCodes.CommodityCode;
				var additionalDetailsObject = {
					MCAT: MCAT,
					LM: LM
				};
				for (var i = 0; i < arrayList.length; i++) {
					if (arrayList[i].Name === "Brand") {
						additionalDetailsObject.Brand = arrayList[i].Value;
					}
					if (arrayList[i].Name === "Series") {
						additionalDetailsObject.Series = arrayList[i].Value;
					}
					if (arrayList[i].Name === "Subject") {
						additionalDetailsObject.Subject = arrayList[i].Value;
					}
					if (arrayList[i].Name === "Author") {
						additionalDetailsObject.Author = arrayList[i].Value;
					}
				}
				_scScreenUtils.setModel(this, "extn_additionalAttributeArray", additionalDetailsObject, null);
				console.log('additionalDetailsObject', additionalDetailsObject);
			},
        // handleShipmentLineShortage: function(
        // shipmentLineModel) {
        //     if (
        //     _wscBackroomPickUpUtils.isShipmentLineMarkedShortage(
        //     this, shipmentLineModel)) {
        //         var shortedShipmentLineModel = null;
        //         shortedShipmentLineModel = {};
        //         shortedShipmentLineModel = _scModelUtils.createModelObjectFromKey("ShipmentLine", shortedShipmentLineModel);
        //         _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShortageQuantity", _scModelUtils.getStringValueFromPath("ShipmentLine.ShortageQty", shipmentLineModel), shortedShipmentLineModel);
        //         _scScreenUtils.setModel(
        //         this, "ShortedShipmentLine", shortedShipmentLineModel, null);
        //     }
        // },
        openItemDetails: function (
            event, bEvent, ctrl, args) {
            var itemDetailsModel = null;
            var callingOrgCode = null;
            itemDetailsModel = _scScreenUtils.getTargetModel(
                this, "ItemDetails", null);
            _wscProductUtils.openItemDetails(
                this, itemDetailsModel);
        },
		updateItemDetails:  function (
            event, bEvent, ctrl, args) {
              var shipmentLineModel = null;
            shipmentLineModel = _scScreenUtils.getModel(
                this, "ShipmentLine");
			var itemId = shipmentLineModel.ShipmentLine.ItemID ; 
			
				_iasUIUtils.callApi(this, {
					Item: {
						ItemID: itemId
					}
				}, "extn_getItemDetails", null);
			
           
        },
        // pickAllQuantity: function(
        // event, bEvent, ctrl, args) {
        //     var quantityTextBoxModel = null;
        //     var shipmentLineModel = null;
        //     var action = null;
        //     var zero = 0;
        //     quantityTextBoxModel = _scScreenUtils.getTargetModel(
        //     this, "QuantityTextBoxModel_Output", null);
        //     shipmentLineModel = _scScreenUtils.getModel(
        //     this, "ShipmentLine");
        //     if (
        //     _scBaseUtils.equals(
        //     _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickComplete", shipmentLineModel), "Y")) {
        //         _iasScreenUtils.toggleHighlight(
        //         this, "LastScannedShipmentLineScreenUId", "errorMsgPnl", "information", "Message_OrderLineAlreadyPicked");
        //     } else if (!(
        //     _scBaseUtils.equals(
        //     _scModelUtils.getNumberValueFromPath("Quantity", quantityTextBoxModel), _scModelUtils.getNumberValueFromPath("ShipmentLine.Quantity", shipmentLineModel)))) {
        //         this.fireEventToUpdateShipmentLineQuantity("fullQty", zero);
        //     }
        // },
        // validateQuantityOnBlur: function(
        // event, bEvent, ctrl, args) {
        //     this.validateAndUpdatePickedQuantity("validate");
        // },
        // increaseQuantity: function(
        // event, bEvent, ctrl, args) {
        //     this.validateAndUpdatePickedQuantity("increase");
        // },
        // decreaseQuantity: function(
        // event, bEvent, ctrl, args) {
        //     this.validateAndUpdatePickedQuantity("decrease");
        // },
        // editQuantityOnEnter: function(
        // event, bEvent, ctrl, args) {
        //     if (
        //     _iasEventUtils.isEnterPressed(
        //     event)) {
        //         this.validateAndUpdatePickedQuantity("update");
        //     } else {
        //         if (
        //         _scBaseUtils.isBooleanTrue(
        //         this.validateAndUpdatePickedQuantity("validate")) && _wscShipmentUtils.IsQuantityValueChanged(
        //         this, "QuantityTextBoxModel_Input", "Quantity", "QuantityTextBoxModel_Output", "Quantity")) {
        //             if (!(
        //             _scWidgetUtils.isWidgetVisible(
        //             this, "updateQtyButton"))) {
        //                 _scWidgetUtils.showWidget(
        //                 this, "updateQtyButton", false, "");
        //             }
        //         } else {
        //             _scWidgetUtils.hideWidget(
        //             this, "updateQtyButton", false);
        //         }
        //     }
        // },
        // validateAndUpdatePickedQuantity: function(
        // action) {
        //     var quantityTxtBoxModel = null;
        //     var currentQty = null;
        //     var maxQty = null;
        //     var zero = 0;
        //     var isQtyInRange = false;
        //     var originalShipmentLineQtyModel = null;
        //     currentQty = _scModelUtils.getNumberValueFromPath("Quantity", _scScreenUtils.getTargetModel(
        //     this, "QuantityTextBoxModel_Output", null));
        //     maxQty = _scModelUtils.getNumberValueFromPath("ShipmentLine.Quantity", _scScreenUtils.getTargetModel(
        //     this, "ShipmentLineQty", null));
        //     var validationConfigBean = null;
        //     validationConfigBean = {};
        //     validationConfigBean["currentQty"] = currentQty;
        //     validationConfigBean["maxQty"] = maxQty;
        //     validationConfigBean["minQty"] = zero;
        //     validationConfigBean["oldQty"] = this.pickedQuantity;
        //     validationConfigBean["minErrorMsg"] = "Message_Picked_Quantity_must_be_greater_than_zero";
        //     validationConfigBean["maxErrorMsg"] = "ItemOverrage_BackroomPick";
        //     validationConfigBean["lastUpdatedRepPanelPropertyName"] = "LastScannedShipmentLineScreenUId";
        //     validationConfigBean["messagePanelId"] = "errorMsgPnl";
        //     isQtyInRange = _iasScreenUtils.ifQtyIsInRangeNew(
        //     this, this, action, "txtScannedQuantity", validationConfigBean, true);
        //     if (
        //     isQtyInRange) {
        //         if (!(
        //         _scBaseUtils.equals(
        //         action, "validate"))) {
        //             this.fireEventToUpdateShipmentLineQuantity("editQty", _scWidgetUtils.getValue(
        //             this, "txtScannedQuantity"));
        //         }
        //     }
        //     return isQtyInRange;
        // },
        // fireEventToUpdateShipmentLineQuantity: function(
        // lineAction, shipmentLineQuantity) {
        //     var quantityTextBoxModel = null;
        //     var shipmentLineModel = null;
        //     var action = null;
        //     var zero = 0;
        //     quantityTextBoxModel = _scScreenUtils.getTargetModel(
        //     this, "ShipmentLineKey_Output", null);
        //     shipmentLineModel = _scScreenUtils.getModel(
        //     this, "ShipmentLine");
        //     if (
        //     _scBaseUtils.equals(
        //     this.flowName, "BackroomPick")) {
        //         if (!(
        //         _scBaseUtils.equals(
        //         lineAction, "fullQty"))) {
        //             action = "PickLine";
        //             _scModelUtils.setNumberValueAtModelPath("ShipmentLine.BackroomPickedQuantity", shipmentLineQuantity, quantityTextBoxModel);
        //         } else {
        //             action = "PickAllLine";
        //         }
        //     }
        //     var shipmentModel = null;
        //     shipmentModel = _scScreenUtils.getTargetModel(
        //     this, "ShipmentKey_Output", null);
        //     _scModelUtils.getOrCreateChildModelObject("Shipment.ShipmentLines", shipmentModel);
        //     shipmentModel = _iasScreenUtils.appendChildModelAsArrayToParentModelPath(
        //     shipmentModel, "Shipment.ShipmentLines", quantityTextBoxModel, zero);
        //     _scModelUtils.setStringValueAtModelPath("Shipment.Action", action, shipmentModel);
        //     var eventArgs = null;
        //     var eventDefn = null;
        //     eventDefn = {};
        //     eventArgs = {};
        //     _scBaseUtils.setAttributeValue("inputData", shipmentModel, eventArgs);
        //     _scBaseUtils.setAttributeValue("argumentList", eventArgs, eventDefn);
        //     _scEventUtils.fireEventToParent(
        //     this, "updateShipmentLineDetails", eventDefn);
        // },
        // handleQuantityChange: function(
        // event, bEvent, ctrl, args) {
        //     var remainingQty = null;
        //     _scWidgetUtils.hideWidget(
        //     this, "updateQtyButton", false);
        //     var shipmentLineModel = null;
        //     shipmentLineModel = _scBaseUtils.getModelValueFromBean("ShipmentLine", args);
        //     remainingQty = _wscBackroomPickUpUtils.getFormattedRemainingQuantity(
        //     this, shipmentLineModel);
        //     _scWidgetUtils.setValue(
        //     this, "remainingQty", remainingQty, false);
        //     var shortageQty = 0;
        //     shortageQty = _scModelUtils.getNumberValueFromPath("ShipmentLine.ShortageQty", shipmentLineModel);
        //     if (
        //     shortageQty >= 1) {
        //         this.handleShipmentLineShortage(
        //         shipmentLineModel);
        //     } else {
        //         _wscBackroomPickUpUtils.validatePickedQuantity(
        //         this, shipmentLineModel, "productScanImagePanel", "completelyPickedLabel", "productScanShortageImagePanel", "updateShortageResolutionImage", "shortageResolutionLink");
        //     }
        // },
        // updateScannedProductQuantity: function(
        // event, bEvent, ctrl, args) {
        //     var shipmentLineModel = null;
        //     var shipmentLinePickedQuantity = null;
        //     shipmentLineModel = _scBaseUtils.getModelValueFromBean("inputData", args);
        //     var quantityTextBoxModel = null;
        //     quantityTextBoxModel = {};
        //     shipmentLinePickedQuantity = _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickedQuantity", shipmentLineModel);
        //     this.pickedQuantity = shipmentLinePickedQuantity;
        //     _scModelUtils.setStringValueAtModelPath("Quantity", shipmentLinePickedQuantity, quantityTextBoxModel);
        //     _scScreenUtils.setModel(
        //     this, "QuantityTextBoxModel_Input", quantityTextBoxModel, null);
        //     _scScreenUtils.setModel(
        //             this, "QuantityReadOnlyModel_Input", quantityTextBoxModel, null);
        // },
        // updatePickedQuantity: function(
        // event, bEvent, ctrl, args) {
        //     this.validateAndUpdatePickedQuantity("update");
        // },
        // validatePickedQuantityBeforeUpdate: function(
        // pickedQtyModel) {
        //     var isValid = true;
        //     var originalShipmentLineQtyModel = null;
        //     originalShipmentLineQtyModel = _scScreenUtils.getTargetModel(
        //     this, "ShipmentLineQty", null);
        //     if (
        //     _scModelUtils.getNumberValueFromPath("Quantity", pickedQtyModel) > _scModelUtils.getNumberValueFromPath("ShipmentLine.Quantity", originalShipmentLineQtyModel)) {
        //         _iasScreenUtils.showErrorMessageBoxWithOk(
        //         this, "Message_Picked_Quantity_cannot_exceed_original_quantity");
        //     }
        //     if (!(
        //     _wscBackroomPickUpUtils.isQuantityChanged(
        //     this.pickedQuantity, _scModelUtils.getStringValueFromPath("Quantity", pickedQtyModel)))) {
        //         isValid = false;
        //     }
        //     return isValid;
        // },
        // displayInvalidQuantityErrorBox: function() {
        //     _iasScreenUtils.showErrorMessageBoxWithOk(
        //     this, "Message_InvalidQuantityData");
        // },
        // openShortageResolutionPopup: function(
        // event, bEvent, ctrl, args) {
        //     var screenInputModel = null;
        //     var backroomPickedQuantity = null;
        //     var shortageQuantity = null;
        //     var zero = 0;
        //     var codeType = null;
        //     screenInputModel = _scScreenUtils.getTargetModel(
        //     this, "Shortage_Output", null);
        //     if (
        //     _scBaseUtils.equals("CustomerPick", this.flowName)) {
        //         codeType = "YCD_CUST_VERFN_TYP";
        //     } else if (
        //     _scBaseUtils.equals("BackroomPick", this.flowName)) {
        //         codeType = "YCD_PICK_SHORT_RESOL";
        //     } else if (
        //     _scBaseUtils.equals("ContainerPack", this.flowName)) {
        //         codeType = "YCD_PACK_SHORT_RESOL";
        //     }
        //     _scModelUtils.setStringValueAtModelPath("CommonCode.CodeType", codeType, screenInputModel);
        //     var shipmentLineModel = null;
        //     shipmentLineModel = _scScreenUtils.getModel(
        //     this, "ShipmentLine");
        //     backroomPickedQuantity = _scModelUtils.getNumberValueFromPath("ShipmentLine.BackroomPickedQuantity", shipmentLineModel);
        //     if (!(
        //     _iasUIUtils.isValueNumber(
        //     backroomPickedQuantity))) {
        //         backroomPickedQuantity = zero;
        //     }
        //     _scModelUtils.setNumberValueAtModelPath("ShipmentLine.DisplayQty", backroomPickedQuantity, shipmentLineModel);
        //     _scModelUtils.setStringValueAtModelPath("ShipmentLine.DisplayShortQty", _wscShipmentUtils.subtract(
        //     _scModelUtils.getNumberValueFromPath("ShipmentLine.Quantity", shipmentLineModel), backroomPickedQuantity), shipmentLineModel);
        //     var bindings = null;
        //     bindings = {};
        //     var screenConstructorParams = null;
        //     screenConstructorParams = {};
        //     _scModelUtils.addStringValueToModelObject("flowName", this.flowName, screenConstructorParams);
        //     bindings["ShipmentLine"] = shipmentLineModel;
        //     var popupParams = null;
        //     popupParams = {};
        //     popupParams["screenInput"] = screenInputModel;
        //     popupParams["outputNamespace"] = "ShortedShipmentLineModel";
        //     popupParams["binding"] = bindings;
        //     popupParams["screenConstructorParams"] = screenConstructorParams;
        //     var dialogParams = null;
        //     dialogParams = {};
        //     dialogParams["closeCallBackHandler"] = "onShortageReasonSelection";
        //     dialogParams["class"] = "popupTitleBorder fixedActionBarDialog";
        //     _iasUIUtils.openSimplePopup("wsc.components.shipment.common.screens.ShortageReasonPopup", "Title_ShortageReason", this, popupParams, dialogParams);
        // },
        // onShortageReasonSelection: function(
        // actionPerformed, model, popupParams) {
        //     if (!(
        //     _scBaseUtils.equals(
        //     actionPerformed, "CLOSE"))) {
        //         var shortedShipmentLineModel = null;
        //         shortedShipmentLineModel = _scScreenUtils.getModel(
        //         this, "ShortedShipmentLineModel");
        //         this.handleShortageResolution(
        //         shortedShipmentLineModel);
        //     }
        // },
        // handleShortageResolution: function(
        // shortedShipmentLineModel) {
        //     var argBean = null;
        //     argBean = {};
        //     argBean["ShortedShipmentLine"] = shortedShipmentLineModel;
        //     this.callMultiApiShortageUpdate(
        //     argBean);
        // },
        // callMultiApiShortageUpdate: function(
        // args) {
        //     var shortedShipmentLineModel = null;
        //     var shipmentLinePickedModel = null;
        //     shipmentLinePickedModel = _scScreenUtils.getTargetModel(
        //     this, "ShipmentLine_Output", null);
        //     shortedShipmentLineModel = _scBaseUtils.getModelValueFromBean("ShortedShipmentLine", args);
        //     var updateShortageModel = null;
        //     updateShortageModel = _wscBackroomPickUpUtils.getShortageChangeShipmentModel(
        //     shipmentLinePickedModel, args);
        //     if (
        //     _scBaseUtils.equals("Y", _scModelUtils.getStringValueFromPath("ShipmentLine.MarkAllShortLineWithShortage", shortedShipmentLineModel))) {
        //         var shipmentModel = null;
        //         shipmentModel = {};
        //         shipmentModel = _scModelUtils.createModelObjectFromKey("Shipment", shipmentModel);
        //         _scModelUtils.setStringValueAtModelPath("Shipment.Action", "MarkAllLinesShortage", shipmentModel);
        //         _scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", updateShortageModel), shipmentModel);
        //         updateShortageModel = shipmentModel;
        //     } else {
        //         _scModelUtils.setStringValueAtModelPath("Shipment.Action", "MarkLineAsShortage", updateShortageModel);
        //     }
        //     _scModelUtils.setStringValueAtModelPath("Shipment.ShortageReasonCode", _scModelUtils.getStringValueFromPath("ShipmentLine.ShortageReasonCode", shortedShipmentLineModel), updateShortageModel);
        //     var eventArgs = null;
        //     var eventDefn = null;
        //     eventDefn = {};
        //     eventArgs = {};
        //     _scBaseUtils.setAttributeValue("inputData", updateShortageModel, eventArgs);
        //     _scBaseUtils.setAttributeValue("argumentList", eventArgs, eventDefn);
        //     _scEventUtils.fireEventToParent(
        //     this, "updateShipmentLineDetails", eventDefn);
        // },
        getFormattedDisplayQuantity: function (dataValue, screen, widget, namespace, modelObj, options) {
            return _wscShipmentUtils.getFormattedDisplayQuantity(dataValue, screen, widget, namespace, _scScreenUtils.getModel(this, "ShipmentLine"), options);
        },
        getFormattedRemainingQuantity: function (
            dataValue, screen, widget, namespace, modelObj, options) {
            dataValue = _wscBackroomPickUpUtils.getFormattedRemainingQuantity(
                this, modelObj);
            return dataValue;
        },
        giftWrapAction: function () {
            // var shipmentLineModel = null;

            // shipmentLineModel = _scScreenUtils.getModel(
            //     this, "ShipmentLine");
            // var shipmentKey = "";
            // if (shipmentLineModel && shipmentLineModel.ShipmentLine) {
            //     shipmentKey = shipmentLineModel.ShipmentLine.ShipmentKey;
            // }
            // var changeShipmentInput = {
            //     Shipment: {
            //         ShipmentKey: shipmentKey,
            //         BaseDropStatus: "1100.70.300",
            //         TransactionId: "Gift Wrap.0001.ex"
            //     }
            // };
            // _iasUIUtils.callApi(this, changeShipmentInput, "extn_printStatus", null);
            console.log('hi grandma '); 
            //console.log('changeShipmentInput', changeShipmentInput);



            
            //this.callGlobal();
            // _scEventUtils.fireEventGlobally(this, "refreshPageAfterGiftWrap", null, {});
            // var wizInstance = _iasUIUtils.getWizardForScreen(this);
            // _scWizardUtils.closeWizard(wizInstance);

            // var editorInstance = _scEditorUtils.getCurrentEditor();
            // _scEditorUtils.refresh(editorInstance);
        }
    });
});