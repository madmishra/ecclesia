scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/BaseTemplateUtils", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/EventUtils", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/RepeatingPanelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!wsc/components/common/utils/CommonUtils", "scbase/loader!extn/customScreen/sort/SortScanPickUI", "scbase/loader!wsc/components/shipment/backroompick/utils/BackroomPickUpUtils", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils", "scbase/loader!dojo/dom-class", "scbase/loader!dojo/dnd/Target"], function (
    _dojodeclare, _iasBaseTemplateUtils, _iasContextUtils, _iasEventUtils, _iasRepeatingScreenUtils, _iasScreenUtils, _iasUIUtils, _iasWizardUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scRepeatingPanelUtils, _scScreenUtils, _scWidgetUtils, _scWizardUtils, _wscCommonUtils, _extnSortScanPickUI, _wscBackroomPickUpUtils, _wscShipmentUtils) {
    return _dojodeclare("extn.customScreen.sort.SortScanPick", [_extnSortScanPickUI], {
        // custom code here
        afterScreenLoad: function (
            event, bEvent, ctrl, args) {
            if (!(
                _iasContextUtils.isMobileContainer())) {
                _scWidgetUtils.setFocusOnWidgetUsingUid(
                    this, "scanProductIdTxt");
            }
            var wizardInstance = false;
            wizardInstance = _iasUIUtils.getParentScreen(
                this, true);
            _iasWizardUtils.hideNavigationalWidget(
                wizardInstance, "prevBttn", false);
            inputShipmentModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            debugger
            var shipKey = inputShipmentModel.Shipment.ShipmentKey;
            _iasUIUtils.callApi(
                this, { Shipment: { ShipmentKey: shipKey } }, "changeFlagForSort_ref", null);
        },
        initializeScreen: function (
            event, bEvent, ctrl, args) {
            var inputShipmentModel = null;
            var eventBean = null;
            eventBean = {};
            inputShipmentModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            _iasScreenUtils.hideSystemMessage(
                this);
            if (
                _iasContextUtils.isMobileContainer()) {
                _scWidgetUtils.hideWidget(
                    this, "shipmentDetails", false);
                var blankModel = null;
                blankModel = {};
                _scBaseUtils.setAttributeValue("argumentList", blankModel, eventBean);
                _scBaseUtils.setAttributeValue("argumentList.Shipment", inputShipmentModel, eventBean);
                _scEventUtils.fireEventToParent(
                    this, "setWizardDescription", eventBean);

            }
            var wizardScreen = null;
            var pickupOrderNo = null;
            var deliveryMethod = null;
            var wizardTitleKey = null;
            var shipmentTypeModel = null;
            shipmentTypeModel = {};
            shipmentTypeModel = _scModelUtils.createModelObjectFromKey("Shipment", shipmentTypeModel);
            pickupOrderNo = _wscCommonUtils.getDisplayOrderNumber(
                _scModelUtils.getStringValueFromPath("Shipment.DisplayOrderNo", inputShipmentModel), "LITERAL");
            _iasScreenUtils.updateEditorTitle(
                this, pickupOrderNo, "Title_Backroom_Pickup_Order");
            deliveryMethod = _scModelUtils.getStringValueFromPath("Shipment.DeliveryMethod", inputShipmentModel);
            _scModelUtils.setStringValueAtModelPath("Shipment.DeliveryMethod", deliveryMethod, shipmentTypeModel);
            _iasUIUtils.setWizardModel(
                this, "ShipmentType", shipmentTypeModel, null);
            if (
                _scBaseUtils.equals(
                    deliveryMethod, "SHP")) {
                wizardTitleKey = "Title_BPForShipOrder";
            } else if (
                _scBaseUtils.equals(
                    deliveryMethod, "PICK")) {
                wizardTitleKey = "Title_BPForPickOrder";
            }
            eventBean = {};
            _scBaseUtils.setAttributeValue("argumentList.titleKey", wizardTitleKey, eventBean);
            _scEventUtils.fireEventToParent(
                this, "setWizardTitle", eventBean);
            var urlString = null;
            urlString = _scModelUtils.getStringValueFromPath("Shipment.ImageUrl", inputShipmentModel);
            if (!(
                _scBaseUtils.isVoid(
                    urlString))) {
                var imageUrlModel = null;
                imageUrlModel = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
                _scModelUtils.setStringValueAtModelPath("CommonCode.CodeLongDescription", urlString, imageUrlModel);
                _scModelUtils.setStringValueAtModelPath("CommonCode.CodeShortDescription", _scModelUtils.getStringValueFromPath("Shipment.ImageAltText", inputShipmentModel), imageUrlModel);
                _scScreenUtils.setModel(
                    this, "clockImageBindingValues", imageUrlModel, null);
            }
        },
        proceedToSummaryScreen: function (
            shipmentModel) {
            _iasUIUtils.openWizardInEditor("wsc.components.shipment.summary.ShipmentSummaryWizard", shipmentModel, "wsc.desktop.editors.ShipmentEditor", this, null);
        },
        canProceedToNextScreen: function (
            shipmentLineList) {
            var action = null;
            action = _scModelUtils.getStringValueFromPath("ShipmentLines.NextAction", shipmentLineList);
            if (
                _scBaseUtils.equals(
                    action, "GotoNextScreen")) {
                this.gotoNextScreen();
            } else if (
                _scBaseUtils.equals(
                    action, "ShowCancelPopup")) {
                _scScreenUtils.showWarningMessageBoxWithOk(
                    this, _scScreenUtils.getString(
                        this, "Message_OrderWillBeCancelled"), "handleCancelOrderConfirmation", null, null);
            } else if (
                _scBaseUtils.equals(
                    action, "ShowLinesNotPickedPopup")) {
                _scScreenUtils.showConfirmMessageBox(
                    this, _scScreenUtils.getString(
                        this, "Message_NotAllLinesPicked"), "handleNotPickedLinesConfirmation", null, null);
            }
        },
        showHideShipmentLineList: function (
            shipmentLineList) {
            if (
                _scBaseUtils.equals("0", _scModelUtils.getStringValueFromPath("ShipmentLines.TotalNumberOfRecords", shipmentLineList))) {
                _scWidgetUtils.showWidget(
                    this, "allLinesPickedLabel", false, null);
                _scWidgetUtils.hideWidget(
                    this, "tpProductLines", false);
                _scWidgetUtils.hideWidget(
                    this, "lastProductScannedDetailsScreenRef", false);
                _scWidgetUtils.hideWidget(
                    this, "pickAll", false);
                _scWidgetUtils.hideWidget(
                    this, "productScanForm", false);
                _scWidgetUtils.hideWidget(
                    this, "viewShortItemsLink", false);
                if (
                    _iasContextUtils.isMobileContainer()) {
                    _scRepeatingPanelUtils.hideMobilePaginationPreviousButton(
                        this, this.activeRepeatingPanelUId, false);
                    _scRepeatingPanelUtils.hideMobilePaginationNextButton(
                        this, this.activeRepeatingPanelUId, false);
                } else {
                    _scRepeatingPanelUtils.hideDesktopPaginationBar(
                        this, this.activeRepeatingPanelUId, false);
                }
            } else {
                this.nextView = "shortItems";
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "reloadSelectView", eDef, eArgs);
            }
        },
        isPickComplete: function (
            shipmentLineList) {
            var numOfRecords = null;
            numOfRecords = _scModelUtils.getStringValueFromPath("ShipmentLines.TotalNumberOfRecords", shipmentLineList);
            if (
                _scBaseUtils.equals(
                    numOfRecords, "0")) {
                this.gotoNextScreen();
            } else {
                this.onlyShortageLinesDisplayed = true;
                this.updateShipmentLineListPanel(
                    shipmentLineList, "SHORTEDLINES");
            }
        },
        updateShipmentLineListPanel: function (
            shipmentLineList, displayType) {
            if (
                _scBaseUtils.equals(
                    displayType, "SHORTEDLINES")) {
                _scWidgetUtils.setValue(
                    this, "viewShortItemsLink", _scScreenUtils.getString(
                        this, "Action_View_All_Items"), false);
                var numOfRecords = null;
                numOfRecords = _scModelUtils.getStringValueFromPath("ShipmentLines.TotalNumberOfRecords", shipmentLineList);
                if (
                    _scBaseUtils.equals(
                        numOfRecords, "0")) {
                    _scWidgetUtils.destroyWidget(
                        this, "shipmentLineDetailsContainer");
                    return;
                }
            } else if (
                _scBaseUtils.equals(
                    displayType, "ALLLINES")) {
                _scWidgetUtils.setValue(
                    this, "viewShortItemsLink", _scScreenUtils.getString(
                        this, "Action_View_Short_Items"), false);
            }
            var bindingData = null;
            bindingData = {};
            bindingData = _scModelUtils.createModelObjectFromKey("BindingData", bindingData);
            _scModelUtils.setStringValueAtModelPath("BindingData.repeatingscreenGeneratorFunction", "getShipmentLinesRepeatingScreenData", bindingData);
            _scModelUtils.setStringValueAtModelPath("BindingData.uniqueIdGeneratorFunc", "getUniqueRepeatingPanelUId", bindingData);
            _scModelUtils.setStringValueAtModelPath("BindingData.RepeatingScreenData.sourcenamespace", "backroomPickShipmentLineList_output", bindingData);
            _scModelUtils.setStringValueAtModelPath("BindingData.RepeatingScreenData.repeatingnamespace", "ShipmentLine", bindingData);
            _scModelUtils.setStringValueAtModelPath("BindingData.RepeatingScreenData.repeatingscreenpath", "ShipmentLine", bindingData);
            _scModelUtils.setStringValueAtModelPath("BindingData.RepeatingScreenData.path", "ShipmentLines.ShipmentLine", bindingData);
            _wscShipmentUtils.createShipmentLines(
                this, "shipmentLineDetailsContainer", "repeatingIdentifierScreen", bindingData, shipmentLineList);
        },
        updateShipmentLineDetails: function (
            event, bEvent, ctrl, args) {
            this.backroomPickSaved = false;
            var shipmentModel = null;
            shipmentModel = _scBaseUtils.getModelValueFromBean("inputData", args);
            _iasUIUtils.callApi(
                this, shipmentModel, "updateShipmentQuantityForPickAllLine", null);
        },
        beforePagingLoad: function (
            event, bEvent, ctrl, args) {
            var labelKey = null;
            var numOfRecords = null;
            numOfRecords = _scModelUtils.getStringValueFromPath("Page.Output.ShipmentLines.TotalNumberOfRecords", _scScreenUtils.getModel(
                this, "backroomPickShipmentLineList_output"));
            if (
                this.onlyShortageLinesDisplayed) {
                labelKey = "Action_View_All_Items";
            } else {
                labelKey = "Action_View_Short_Items";
            }
            _scWidgetUtils.setValue(
                this, "viewShortItemsLink", _scScreenUtils.getString(
                    this, labelKey), false);
        },
        handleReloadScreen: function (
            event, bEvent, ctrl, args) {
            _iasScreenUtils.hideSystemMessage(
                this);
            var shipmentLineModel = null;
            shipmentLineModel = {};
            shipmentLineModel = _scModelUtils.createModelObjectFromKey("ShipmentLine", shipmentLineModel);
            var shipmentDetailsModel = null;
            shipmentDetailsModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailsModel), shipmentLineModel);
            _iasUIUtils.callApi(
                this, shipmentLineModel, "getNotPickedShipmentLineListCount", null);
        },
        customError: function (
            screen, data, code) {
            var errorMessageBundle = null;
            if (
                _scBaseUtils.equals(
                    code, "YCD00063")) {
                errorMessageBundle = "InvalidBarCodeData";
            } else if (
                _scBaseUtils.equals(
                    code, "YCP0187")) {
                errorMessageBundle = "BarCodeDataRequired";
            } else if (
                _scBaseUtils.equals(
                    code, "YCD00064")) {
                errorMessageBundle = "MultipleItemsFound";
            } else if (
                _scBaseUtils.equals(
                    code, "YCD00065")) {
                errorMessageBundle = "ItemNotFound";
            } else if (
                _scBaseUtils.equals(
                    code, "YCD00066")) {
                errorMessageBundle = "ItemNotInShipment";
            } else if (
                _scBaseUtils.equals(
                    code, "YCD00067")) {
                errorMessageBundle = "ItemOverrage_BackroomPick";
            } else {
                return false;
            }
            if (!(
                _scBaseUtils.isVoid(
                    errorMessageBundle))) {
                _iasScreenUtils.showErrorMessageBoxWithOk(
                    this, errorMessageBundle);
                return true;
            }
        },
        save: function (
            event, bEvent, ctrl, args) {
            if (
                _iasWizardUtils.isWizardPageDirty(
                    _iasUIUtils.getParentScreen(
                        this, true))) {
                _scScreenUtils.showConfirmMessageBox(
                    this, _scScreenUtils.getString(
                        this, "Message_DirtyNextMessage"), "handleNextDirtyConfirmation", null, null);
            } else {
                this.validateShipmentStatusOnNext();
            }
        },
        handleNextDirtyConfirmation: function (
            res) {
            if (
                _scBaseUtils.equals(
                    res, "Ok")) {
                this.validateShipmentStatusOnNext();
            }
        },
        validateShipmentStatusOnNext: function () {
            this.nextButtonClicked = true;
            var shipmentLineModel = null;
            shipmentLineModel = {};
            shipmentLineModel = _scModelUtils.createModelObjectFromKey("ShipmentLine", shipmentLineModel);
            var shipmentDetailsModel = null;
            shipmentDetailsModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailsModel), shipmentLineModel);
            _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentDeliveryMethod", _scModelUtils.getStringValueFromPath("Shipment.DeliveryMethod", shipmentDetailsModel), shipmentLineModel);
            _iasUIUtils.callApi(
                this, shipmentLineModel, "validateChangeShipmentStatusOnNext", null);
        },
        gotoNextScreen: function () {
            _scScreenUtils.clearScreen(
                this, "translateBarCode_input");
            var parentScreen = null;
            parentScreen = _iasUIUtils.getParentScreen(
                this, true);
            if (
                _scWizardUtils.isCurrentPageLastEntity(
                    parentScreen)) {
                _iasWizardUtils.setActionPerformedOnWizard(
                    parentScreen, "CONFIRM");
            } else {
                _iasWizardUtils.setActionPerformedOnWizard(
                    parentScreen, "NEXT");
            }
            _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
        },
        saveSucces: function (
            mashupRefId, mashupContext) {
            if (
                _scBaseUtils.equals(
                    mashupRefId, "saveShipmentStatus")) {
                this.backroomPickSaved = true;
            }
            if (
                _wscBackroomPickUpUtils.isLastMashupRefId(
                    mashupRefId, mashupContext)) {
                this.gotoNextScreen();
            }
        },
        getShipmentLineModelByAction: function (
            action) {
            var shipmentLineModel = null;
            shipmentLineModel = {};
            shipmentLineModel = _scModelUtils.createModelObjectFromKey("ShipmentLine", shipmentLineModel);
            var shipmentDetailsModel = null;
            shipmentDetailsModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailsModel), shipmentLineModel);
            if (
                _scBaseUtils.equals(
                    action, "SHORTLINES")) {
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.BackroomPickComplete", "Y", shipmentLineModel);
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.BackroomPickCompleteQryType", "NE", shipmentLineModel);
            }
            return shipmentLineModel;
        },
        displayShortItems: function (
            event, bEvent, ctrl, args) {
            var inputModel = null;
            var labelKey = "Action_View_All_Items";
            if (!(
                this.onlyShortageLinesDisplayed)) {
                this.onlyShortageLinesDisplayed = true;
                inputModel = this.getShipmentLineModelByAction("SHORTLINES");
            } else {
                labelKey = "Action_View_Short_Items";
                this.onlyShortageLinesDisplayed = false;
                inputModel = this.getShipmentLineModelByAction("ALLINES");
            }
            _scWidgetUtils.setValue(
                this, "viewShortItemsLink", _scScreenUtils.getString(
                    this, labelKey), false);
            _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "repeatingIdentifierScreen", "backroomPickShipmentLineList_output", inputModel);
        },
        scanProductOnEnter: function (
            event, bEvent, ctrl, args) {
            if (
                _iasEventUtils.isEnterPressed(
                    event)) {
                this.scanProduct();
            }
        },
        scanProduct: function () {
            var barCodeModel = null;
            var barCodeData = null;
            if (!(
                _scScreenUtils.isValid(
                    this, "translateBarCode_input"))) {
                _iasScreenUtils.showErrorMessageBoxWithOk(
                    this, "InvalidBarCodeData");
            } else {
                barCodeModel = _scScreenUtils.getTargetModel(
                    this, "translateBarCode_input", null);
                barCodeData = _scModelUtils.getStringValueFromPath("BarCode.BarCodeData", barCodeModel);
                if (
                    _scBaseUtils.isVoid(
                        barCodeData)) {
                    _iasScreenUtils.showErrorMessageBoxWithOk(
                        this, "NoProductScanned");
                } else {
                    _iasUIUtils.callApi(
                        this, barCodeModel, "translateBarCode", null);
                    var eventDefn = null;
                    var eventArgs = null;
                    eventArgs = {};
                    eventDefn = {};
                    _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
                    this.clearItemFilter();
                }
            }
        },
        setFocusOnScanField: function (
            event, bEvent, ctrl, args) {
            if (!(
                _iasContextUtils.isMobileContainer())) {
                _scWidgetUtils.setFocusOnWidgetUsingUid(
                    this, "scanProductIdTxt");
            }
        },
        pickAll: function (
            event, bEvent, ctrl, args) {
            if (
                this.backroomPickSaved) {
                _iasBaseTemplateUtils.showMessage(
                    this, "Message_AllTheOrderLinesArePickedCompletely", "information", null);
            } else {
                var shipmentModel = null;
                shipmentModel = _scScreenUtils.getTargetModel(
                    this, "updateShipmentPickQuantity_input", null);
                _scModelUtils.setStringValueAtModelPath("Shipment.Action", "PickAll", shipmentModel);
                _iasUIUtils.callApi(
                    this, shipmentModel, "updateShipmentQuantityForPickAll", null);
            }
        },
        updateProductQuantity: function (
            translateBarCodeOutputModel) {
            var eventArgs = null;
            var eventDefn = null;
            eventDefn = {};
            eventArgs = {};
            var shipmentLineScannedModel = null;
            shipmentLineScannedModel = _scModelUtils.getModelObjectFromPath("BarCode.Shipment", translateBarCodeOutputModel);
            var repPanelUId = null;
            repPanelUId = _scRepeatingPanelUtils.returnUIdOfIndividualRepeatingPanel(
                this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineScannedModel));
            var repPanelScreen = null;
            repPanelScreen = _iasScreenUtils.getRepeatingPanelScreenWidget(
                this, repPanelUId);
            if (!(
                _scBaseUtils.isVoid(
                    repPanelScreen))) {
                var quantityTextBoxModel = null;
                var shipmentLinePickedQuantity = null;
                quantityTextBoxModel = {};
                shipmentLinePickedQuantity = _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickedQuantity", shipmentLineScannedModel);
                _wscBackroomPickUpUtils.setPickedQuantityInScreen(
                    repPanelScreen, shipmentLinePickedQuantity);
                _scModelUtils.setStringValueAtModelPath("Quantity", shipmentLinePickedQuantity, quantityTextBoxModel);
                _scBaseUtils.setAttributeValue("ShipmentLine", shipmentLineScannedModel, eventArgs);
                _scRepeatingPanelUtils.setModelForIndividualRepeatingPanel(
                    this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineScannedModel), "ShipmentLine", shipmentLineScannedModel, null);
                _scRepeatingPanelUtils.setModelForIndividualRepeatingPanel(
                    this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineScannedModel), "QuantityTextBoxModel_Input", quantityTextBoxModel, null);
                _scRepeatingPanelUtils.setModelForIndividualRepeatingPanel(
                    this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineScannedModel), "QuantityReadOnlyModel_Input", quantityTextBoxModel, null);
                _scScreenUtils.clearScreen(
                    repPanelScreen, "QuantityTextBoxModel_Output");
                var args = null;
                eventDefn = {};
                args = {};
                _scBaseUtils.setAttributeValue("ShipmentLine", shipmentLineScannedModel, args);
                _scBaseUtils.setAttributeValue("argumentList", args, eventDefn);
                _scEventUtils.fireEventToChild(
                    this, repPanelUId, "handleQuantityChange", eventDefn);
            }
            _iasScreenUtils.toggleHighlight(
                this, repPanelScreen, "LastScannedShipmentLineScreenUId", "errorMsgPnl", "success", "Message_PickedQuantityUpdatedSuccessfully");
            var childScreen = null;
            childScreen = _scScreenUtils.getChildScreen(
                this, "lastProductScannedDetailsScreenRef");
            _scScreenUtils.setModel(
                this, "lastProductScanned_output", shipmentLineScannedModel, null);
            if (!(
                _scBaseUtils.isVoid(
                    childScreen))) {
                _scScreenUtils.clearScreen(
                    childScreen, "PickedQuantity_Output");
                if (!(
                    _scWidgetUtils.isWidgetVisible(
                        this, "lastProductScannedDetailsScreenRef"))) {
                    _scWidgetUtils.showWidget(
                        this, "lastProductScannedDetailsScreenRef", false, null);
                }
                _scEventUtils.fireEventInsideScreen(
                    childScreen, "updateShipmentLineQuantity", eventDefn, eventArgs);
            } else {
                _scScreenUtils.showChildScreen(
                    this, "lastProductScannedDetailsScreenRef", null, null, null, shipmentLineScannedModel);
            }
            if (
                _scBaseUtils.isVoid(
                    repPanelScreen)) {
                childScreen = _scScreenUtils.getChildScreen(
                    this, "lastProductScannedDetailsScreenRef");
            }
            var shipmentLineKey = null;
            shipmentLineKey = _scModelUtils.getStringValueFromPath("BarCode.Shipment.ShipmentLine.ShipmentLineKey", translateBarCodeOutputModel);
            this.lastScannedItemShipmentLineKey = shipmentLineKey;
        },
        getShipmentLinesRepeatingScreenData: function (
            value, screen, widget, namespace, modelObject) {
            var repeatingScreenId = "extn.customScreen.sort.SortLineDetails";
            var returnValue = null;
            var additionalParamsBean = null;
            additionalParamsBean = {};
            additionalParamsBean["flowName"] = this.flowName;
            var namespaceMapBean = null;
            namespaceMapBean = {};
            namespaceMapBean["parentnamespace"] = "ItemScan_Output";
            namespaceMapBean["childnamespace"] = "ShipmentLine_Output";
            namespaceMapBean["parentpath"] = "Shipment.ShipmentLines.ShipmentLine";
            namespaceMapBean["childpath"] = "ShipmentLine";
            returnValue = _iasScreenUtils.getRepeatingScreenData(
                repeatingScreenId, namespaceMapBean, additionalParamsBean);
            return returnValue;
        },
        getUniqueRepeatingPanelUId: function (
            screen, widget, namespace, dataObject, modelObject, repeatingScreenObject, repeatingScreenIndex) {
            var shipmentLineKey = null;
            shipmentLineKey = _scModelUtils.getStringValueFromPath("ShipmentLineKey", dataObject);
            return shipmentLineKey;
        },
        handleNotPickedLinesConfirmation: function (
            res) {
            if (
                _scBaseUtils.equals(
                    res, "Ok")) {
                this.gotoNextScreen();
            } else {
                this.nextView = "shortItems";
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "reloadSelectView", eDef, eArgs);
            }
        },
        handleCancelOrderConfirmation: function (
            res) {
            _scEventUtils.fireEventToParent(
                this, "closeBttn_onClick", null);
        },
        handlePickAllConfirmation: function (
            res) {
            var childScreen = null;
            childScreen = _scScreenUtils.getChildScreen(
                this, "lastProductScannedDetailsScreenRef");
            if (!(
                _scBaseUtils.isVoid(
                    childScreen))) {
                _scScreenUtils.clearScreen(
                    childScreen, "PickedQuantity_Output");
                if (
                    _scWidgetUtils.isWidgetVisible(
                        this, "lastProductScannedDetailsScreenRef")) {
                    _scWidgetUtils.hideWidget(
                        this, "lastProductScannedDetailsScreenRef", false);
                }
            }
            var shipmentDetailsModel = null;
            shipmentDetailsModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            if (
                _scBaseUtils.equals(
                    res, "Ok")) {
                var shipmentStatusInputModel = null;
                var deliveryMethod = null;
                shipmentStatusInputModel = {};
                shipmentStatusInputModel = _scModelUtils.createModelObjectFromKey("Shipment", shipmentStatusInputModel);
                _scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailsModel), shipmentStatusInputModel);
                deliveryMethod = _scModelUtils.getStringValueFromPath("Shipment.DeliveryMethod", shipmentDetailsModel);
                if (
                    _scBaseUtils.equals(
                        deliveryMethod, "SHP")) {
                    _iasUIUtils.callApi(
                        this, shipmentStatusInputModel, "saveShipmentStatusForShipOrder", null);
                } else if (
                    _scBaseUtils.equals(
                        deliveryMethod, "PICK")) {
                    _iasUIUtils.callApi(
                        this, shipmentStatusInputModel, "saveShipmentStatusForPickUpOrder", null);
                }
            } else {
                this.nextView = "allItems";
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "reloadSelectView", eDef, eArgs);
            }
        },
        clearItemFilter: function (
            event, bEvent, ctrl, args) {
            var tempModel = null;
            tempModel = {};
            _scScreenUtils.setModel(
                this, "translateBarCode_output", tempModel, null);
            _scWidgetUtils.setFocusOnWidgetUsingUid(
                this, "scanProductIdTxt");
        },
        formatOrderNo: function (
            dataValue, screen, widget, namespace, modelObj, options) {
            var formattedOrderNo = null;
            formattedOrderNo = _wscCommonUtils.getDisplayOrderNumber(
                dataValue, "COMMAS");
            return formattedOrderNo;
        },
        refreshShipmentLineAfterQuantityUpdate: function (
            modelOutput) {
            var eventArgs = null;
            var eventDefn = null;
            eventDefn = {};
            eventArgs = {};
            if (
                _scBaseUtils.equals("MarkAllLinesShortage", _scModelUtils.getStringValueFromPath("Shipment.Action", modelOutput))) {
                _scScreenUtils.showConfirmMessageBox(
                    this, _scScreenUtils.getString(
                        this, "Message_PickAllLinesMessage"), "handlePickAllConfirmation", null, null);
            } else if (
                _scBaseUtils.equals("ShowCancelPopup", _scModelUtils.getStringValueFromPath("Shipment.Action", modelOutput))) {
                _scScreenUtils.showWarningMessageBoxWithOk(
                    this, _scScreenUtils.getString(
                        this, "Message_OrderWillBeCancelled"), "handleCancelOrderConfirmation", _iasUIUtils.getTextOkObjectForMessageBox(
                            this), null, null);
            } else {
                var pickedQtyModel = null;
                pickedQtyModel = {};
                _scModelUtils.setStringValueAtModelPath("Quantity", _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickedQuantity", modelOutput), pickedQtyModel);
                var repPanelUId = null;
                repPanelUId = _scRepeatingPanelUtils.returnUIdOfIndividualRepeatingPanel(
                    this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", modelOutput));
                var shipmentLineModel = null;
                var repPanelScreen = null;
                repPanelScreen = _iasScreenUtils.getRepeatingPanelScreenWidget(
                    this, repPanelUId);
                if (
                    _scBaseUtils.isVoid(
                        repPanelScreen)) { } else {
                    shipmentLineModel = _scScreenUtils.getModel(
                        repPanelScreen, "ShipmentLine");
                    var shortageQty = 0;
                    shortageQty = _scModelUtils.getNumberValueFromPath("ShipmentLine.ShortageQty", modelOutput);
                    if (
                        shortageQty >= 1) {
                        _scModelUtils.setStringValueAtModelPath("ShipmentLine.Quantity", _scModelUtils.getStringValueFromPath("ShipmentLine.Quantity", modelOutput), shipmentLineModel);
                        _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShortageQty", _scModelUtils.getStringValueFromPath("ShipmentLine.ShortageQty", modelOutput), shipmentLineModel);
                    }
                    var backroomPickComplete = null;
                    backroomPickComplete = _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickComplete", modelOutput);
                    if (
                        _scBaseUtils.isVoid(
                            backroomPickComplete)) {
                        backroomPickComplete = null;
                    }
                    _scModelUtils.setStringValueAtModelPath("ShipmentLine.BackroomPickComplete", backroomPickComplete, shipmentLineModel);
                    _scRepeatingPanelUtils.setModelForIndividualRepeatingPanel(
                        this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", modelOutput), "QuantityTextBoxModel_Input", pickedQtyModel, null);
                    _scRepeatingPanelUtils.setModelForIndividualRepeatingPanel(
                        this, this.activeRepeatingPanelUId, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", modelOutput), "QuantityReadOnlyModel_Input", pickedQtyModel, null);
                    _scScreenUtils.clearScreen(
                        repPanelScreen, "QuantityTextBoxModel_Output");
                    _scModelUtils.setNumberValueAtModelPath("ShipmentLine.BackroomPickedQuantity", _scModelUtils.getNumberValueFromPath("ShipmentLine.BackroomPickedQuantity", modelOutput), shipmentLineModel);
                    var args = null;
                    eventDefn = {};
                    args = {};
                    _scBaseUtils.setAttributeValue("ShipmentLine", shipmentLineModel, args);
                    _scBaseUtils.setAttributeValue("argumentList", args, eventDefn);
                    _scEventUtils.fireEventToChild(
                        this, repPanelUId, "handleQuantityChange", eventDefn);
                    _wscBackroomPickUpUtils.setPickedQuantityInScreen(
                        repPanelScreen, _scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickedQuantity", modelOutput));
                    _scBaseUtils.setAttributeValue("ShipmentLine", shipmentLineModel, eventArgs);
                    _iasScreenUtils.toggleHighlight(
                        this, repPanelScreen, "LastScannedShipmentLineScreenUId", "errorMsgPnl", "success", "Message_PickedQuantityUpdatedSuccessfully");
                }
                if (
                    _scBaseUtils.equals(
                        this.lastScannedItemShipmentLineKey, _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", modelOutput))) {
                    _scBaseUtils.setAttributeValue("argumentList", eventArgs, eventDefn);
                    var childScreen = null;
                    childScreen = _scScreenUtils.getChildScreen(
                        this, "lastProductScannedDetailsScreenRef");
                    _scScreenUtils.setModel(
                        this, "lastProductScanned_output", modelOutput, null);
                    if (!(
                        _scBaseUtils.isVoid(
                            childScreen))) {
                        _scScreenUtils.clearScreen(
                            childScreen, "PickedQuantity_Output");
                        if (!(
                            _scWidgetUtils.isWidgetVisible(
                                this, "lastProductScannedDetailsScreenRef"))) {
                            _scWidgetUtils.showWidget(
                                this, "lastProductScannedDetailsScreenRef", false, null);
                        }
                        _scEventUtils.fireEventInsideScreen(
                            childScreen, "updateLastProductScanned", eventDefn, eventArgs);
                    }
                }
            }
        },
        showConfirmMsgBoxAfterPickAll: function (
            modelOutput) {
            this.backroomPickSaved = true;
            _scScreenUtils.showConfirmMessageBox(
                this, _scScreenUtils.getString(
                    this, "Message_PickAllLinesMessage"), "handlePickAllConfirmation", null, null);
        },
        shortItems_onShow: function (
            event, bEvent, ctrl, args) {
            this.activeRepeatingPanelUId = "readyForPickupLineList";
            var shipmentLineModel = null;
            shipmentLineModel = _scScreenUtils.getTargetModel(
                this, "updateShipmentPickQuantity_input", null);
            _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "readyForPickupLineList", "getReadyForPickLines", shipmentLineModel, "getAllShipmentLineList");
        },
        allItems_onShow: function (
            event, bEvent, ctrl, args) {
            this.activeRepeatingPanelUId = "unpickedLineList";
            if (
                _scBaseUtils.equals(
                    this.isInitViewPainted, false)) {
                this.isInitViewPainted = true;
                return;
            }
            var shipmentLineModel = null;
            shipmentLineModel = _scScreenUtils.getTargetModel(
                this, "updateShipmentPickQuantity_input", null);
            _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "unpickedLineList", "getShortageLines", shipmentLineModel, "getNotPickedShipmentLineList");
        },
        shortItems_beforeHide: function (
            event, bEvent, ctrl, args) {
            if (
                _iasRepeatingScreenUtils.isRepeatingScreenDirty(
                    this, "readyForPickupLineList", "extn.customScreen.sort.SortLineDetails")) {
                _scScreenUtils.showConfirmMessageBox(
                    this, _scScreenUtils.getString(
                        this, "Message_GenericDirty"), "handleDirtyForAllItemsView", null, null);
            } else {
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "loadSelectView", eDef, eArgs);
            }
        },
        allItems_beforeHide: function (
            event, bEvent, ctrl, args) {
            if (
                _iasRepeatingScreenUtils.isRepeatingScreenDirty(
                    this, "unpickedLineList", "extn.customScreen.sort.SortLineDetails")) {
                _scScreenUtils.showConfirmMessageBox(
                    this, _scScreenUtils.getString(
                        this, "Message_GenericDirty"), "handleDirtyForUnpickedItemsView", null, null);
            } else {
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "loadSelectView", eDef, eArgs);
            }
        },
        handleDirtyForUnpickedItemsView: function (
            res) {
            if (
                _scBaseUtils.equals(
                    res, "Ok")) {
                _iasRepeatingScreenUtils.clearRepeatingScreenDirty(
                    this, "readyForPickupLineList", "extn.customScreen.sort.SortLineDetails");
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "loadSelectView", eDef, eArgs);
            }
        },
        handleDirtyForAllItemsView: function (
            res) {
            if (
                _scBaseUtils.equals(
                    res, "Ok")) {
                _iasRepeatingScreenUtils.clearRepeatingScreenDirty(
                    this, "unpickedLineList", "extn.customScreen.sort.SortLineDetails");
                var eDef = null;
                eDef = {};
                var eArgs = null;
                eArgs = {};
                _scEventUtils.fireEventInsideScreen(
                    this, "loadSelectView", eDef, eArgs);
            }
        },
        moveAhead: function () {
            var shipmentLineModel = null;
            shipmentLineModel = _scScreenUtils.getModel(
                this, "backroomPickShipmentDetails_output");
            var shipmentKey = "";
            console.log('shipmentLineModel', shipmentLineModel);
            if (shipmentLineModel && shipmentLineModel.Shipment) {
                shipmentKey = shipmentLineModel.Shipment.ShipmentKey;
            }
            var summaryPageInput = {
                Shipment: {
                    ShipmentKey: shipmentKey
                }
            };
                this.invokePrint() ;
            _iasUIUtils.openWizardInEditor("extn.wizards.sort.HoldWizard", summaryPageInput, "wsc.mobile.editors.MobileEditor", this, null);
        },
        printDetails: function (secondaryDetails) {
            var batchModel = _scScreenUtils.getModel(this, "backroomPickShipmentDetails_output");
            var expectedShipmentDate = Date(batchModel.Shipment.ExpectedShipmentDate);
            var printModel = {};
            printModel.PickupDate = Date(expectedShipmentDate);
            printModel.OrderNo = batchModel.Shipment.DisplayOrderNo;
            if (secondaryDetails.Order) {
                if (secondaryDetails.Order.PersonInfoBillTo) {
                    printModel.PrimaryFirstName = secondaryDetails.Order.PersonInfoBillTo.FirstName;
                    printModel.PrimarySecondName = secondaryDetails.Order.PersonInfoBillTo.LastName;
                }
            }
            if (secondaryDetails.Order.OrderLines && secondaryDetails.Order.OrderLines.OrderLine) {
                var tempArray = secondaryDetails.Order.OrderLines.OrderLine;
                if (tempArray && tempArray[0].AdditionalAddresses && tempArray[0].AdditionalAddresses.AdditionalAddress[0] && tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo) {
                    var firstName = tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo.FirstName;
                    var secondName = tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo.LastName;
                    printModel.SecondaryFirstName = firstName;
                    printModel.SecondaryLastName = secondName;
                }
            }
            console.log('Invoking print with the following data - ', printModel);
            if (window.webkit) {
                window.webkit.messageHandlers.invokePrint.postMessage(printModel);
            }
        },
        invokePrint: function () {
            console.log("Invoking print");
            var batchModel = _scScreenUtils.getModel(this, "backroomPickShipmentDetails_output");
            var orderNo = batchModel.Shipment.DisplayOrderNo;
            _iasUIUtils.callApi(this, {
                Order: {
                    OrderNo: orderNo
                }
            }, "extn_getPrintDetails", null);
        }
    });
});