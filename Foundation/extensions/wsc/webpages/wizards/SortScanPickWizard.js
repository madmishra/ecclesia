scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!wsc/components/common/utils/CommonUtils", "scbase/loader!extn/wizards/SortScanPickWizardUI","scbase/loader!sc/plat/dojo/utils/EventUtils"], 
function(
_dojodeclare, _iasUIUtils, _iasWizardUtils, _scBaseUtils, _scModelUtils, _scResourcePermissionUtils, _scScreenUtils, _scWidgetUtils, _scWizardUtils, _wscCommonUtils, _extnSortScanPickWizardUI,_scEventUtils) {
    return _dojodeclare("extn.wizards.SortScanPickWizard", [_extnSortScanPickWizardUI], {
        // custom code here
        setWizardTitle: function(
        event, bEvent, ctrl, args) {
            var titleKey = null;
            var titleDesc = null;
            titleKey = _scBaseUtils.getAttributeValue("titleKey", false, args);
            this.title = titleKey;
        },
        combinedAPICallOnNext: function(
        uiEvent, businessEvent, control, args) {
            var retdata = null;
            retdata = _scBaseUtils.getModelValueFromBean("returnData", args);
            _scWizardUtils.showNextScreenWithUpdate(
            this, retdata);
        },
        setWizardDescription: function(
        event, bEvent, ctrl, args) {
            var shipmentModel = null;
            shipmentModel = _scBaseUtils.getValueFromPath("Shipment", args);
            _scWidgetUtils.showWidget(
            this, "WizardDescriptionPanel", false, null);
            _scScreenUtils.setModel(
            this, "Shipment", shipmentModel, null);
            var urlString = null;
            urlString = _scModelUtils.getStringValueFromPath("Shipment.ImageUrl", shipmentModel);
            if (!(
            _scBaseUtils.isVoid(
            urlString))) {
                var imageUrlModel = null;
                imageUrlModel = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
                _scModelUtils.setStringValueAtModelPath("CommonCode.CodeLongDescription", urlString, imageUrlModel);
                _scModelUtils.setStringValueAtModelPath("CommonCode.CodeShortDescription", _scModelUtils.getStringValueFromPath("Shipment.ImageAltText", shipmentModel), imageUrlModel);
                _scScreenUtils.setModel(
                this, "clockImageBindingValues", imageUrlModel, null);
                _scWidgetUtils.changeImageTitle(
                this, "img_TimeRmnClock", _scModelUtils.getStringValueFromPath("Shipment.ImageAltText", shipmentModel));
            }
        },
        
        changeConfirmButtonLiteralIfLastPage: function(
        		 event, bEvent, ctrl, args) {
            if(_scWizardUtils.isCurrentPageLastEntity(this)) {
                 _iasWizardUtils.setLabelOnNavigationalWidget(
                 this, "confirmBttn", _scScreenUtils.getString(this, "Title_UpdateHoldLocation"));
            }
        },
        afterWizardConfirm: function(
        event, bEvent, ctrl, args) {
            var shipmentModel = null;
            var pickPackRuleDetailsModel = null;
            shipmentModel = _iasUIUtils.getWizardModel(
            this, "ShipmentModel");
            pickPackRuleDetailsModel = _iasUIUtils.getWizardModel(
            this, "RuleDetailsModel");
            if (
            _scResourcePermissionUtils.hasPermission("WSC000019") && _scBaseUtils.and(
            _scBaseUtils.equals(
            _scModelUtils.getStringValueFromPath("Shipment.Status", shipmentModel), "1100.70.06.50"), _scBaseUtils.equals(
            _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", pickPackRuleDetailsModel), "Y"))) {
                var argBean = null;
                argBean = {};
                _scScreenUtils.showConfirmMessageBox(
                this, _scScreenUtils.getString(
                this, "Message_Pack_Shipment_Now"), "handleShipmentPack", null, null);
            } else {
                _iasWizardUtils.handleWizardCloseConfirmation(
                this, "Ok", args);
            }
        },
        handleShipmentPack: function(
        res, args) {
            if (
            _scBaseUtils.equals(
            res, "Ok")) {
                _iasUIUtils.openWizardInEditor("wsc.components.shipment.container.pack.ContainerPackWizard", _scScreenUtils.getInitialInputData(
                this), "wsc.desktop.editors.ShipmentEditor", this, null);
            } else {
                _iasWizardUtils.handleWizardCloseConfirmation(
                this, "Ok", args);
            }
        },
        formatOrderNo: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            var formattedOrderNo = null;
            formattedOrderNo = _wscCommonUtils.getDisplayOrderNumber(
            dataValue, "LITERAL");
            if (
            _scBaseUtils.equals(
            formattedOrderNo, "MULTIPLE")) {
                _scWidgetUtils.setLabel(
                this, "lblOrderNo", _scScreenUtils.getString(
                this, "Title_MultipleOrders"));
                return;
            } else {
                return formattedOrderNo;
            }
        },
		 handleConfirm : function(
            event, bEvent, ctrl, args){
                _scEventUtils.fireEventToChild(this , "SortScanPick" ,"moveAhead" , null )
        }
    });
});