/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Order Management Store (5725-D10)
 *(C) Copyright IBM Corp. 2014 , 2015 All Rights Reserved. , 2015 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/BaseTemplateUtils", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/SearchUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/RepeatingPanelUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!extn/customScreen/search/extnShipmentSearchUI", "scbase/loader!extn/customScreen/search/utils/SearchUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils"], function (
    _dojodeclare, _iasBaseTemplateUtils, _iasContextUtils, _iasSearchUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scRepeatingPanelUtils, _scResourcePermissionUtils, _scScreenUtils, _scWidgetUtils, _extnextnShipmentSearchUI, _extnSearchUtils, _scEditorUtils) {
    return _dojodeclare("extn.customScreen.search.extnShipmentSearch", [_extnextnShipmentSearchUI], {
        // custom code here
        SST_search: function () {

            var targetModel = null;
            var tempModel = null;
            if (!(
                _scScreenUtils.isValid(
                    this, this.SST_getSearchNamespace()))) {
                _iasBaseTemplateUtils.showMessage(
                    this, "InvalidSearchCriteria", "error", null);
                return;
            } else {
                _iasBaseTemplateUtils.hideMessage(
                    this);
                targetModel = _scBaseUtils.getTargetModel(
                    this, this.SST_getSearchNamespace(), null);
                if (
                    _scBaseUtils.equals(
                        this.SST_getSearchNamespace(), "getAdvancedShipmentListWithGiftRct_input")) {
                    tempModel = _scModelUtils.getModelObjectFromPath("Shipment.OrderBy", _scScreenUtils.getTargetModel(
                        this, "getAdvancedShipmentList_input", null));
                    _scModelUtils.addModelToModelPath("Shipment.OrderBy", tempModel, targetModel);
                }
            }
           var includeOtherStores = null;
            includeOtherStores = _scBaseUtils.getTargetModel(
                this, "IncludeOrdersPickedInOtherStore", null);
            if (
                _scBaseUtils.equals(
                    _scModelUtils.getStringValueFromPath("isChecked", includeOtherStores), "Y")) {
                _scModelUtils.setStringValueAtModelPath("Shipment.ShipNode", " ", targetModel);
            } else {
                _scModelUtils.setStringValueAtModelPath("Shipment.ShipNode", _iasContextUtils.getFromContext("CurrentStore"), targetModel);
            }
            _scBaseUtils.removeBlankAttributes(
                targetModel);
            var eventDefn = null;
            var args = null;
            eventDefn = {};
            args = {};
            _scBaseUtils.setAttributeValue("inputData", targetModel, args);
            _scBaseUtils.setAttributeValue("argumentList", args, eventDefn);

            _scEventUtils.fireEventToChild(
                this, "extnShipmentSearchResult", "callListApi", eventDefn);

        },
        onClickAction: function (
            event, bEvent, ctrl, args) {
            var targetModel = null;
            var setModelOptions = null;
            targetModel = _scScreenUtils.getTargetModel(
                this, "IncludeOrdersPickedInOtherStore", null);
            var screenInput = null;
            screenInput = {};
            if (
                _scBaseUtils.equals(
                    _scModelUtils.getStringValueFromPath("isChecked", targetModel), "Y")) {
                _scModelUtils.setStringValueAtModelPath("Shipment.Store", " ", screenInput);
                _scModelUtils.setStringValueAtModelPath("Shipment.EnterpriseCode", _iasContextUtils.getFromContext("EnterpriseCode"), screenInput);
                setModelOptions = {};
                _scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptions);
                _scBaseUtils.setAttributeValue("allowEmpty", true, setModelOptions);
                _scScreenUtils.setModel(
                    this, "screenInput", screenInput, setModelOptions);
            } else {
                _scModelUtils.setStringValueAtModelPath("Shipment.Store", _iasContextUtils.getFromContext("CurrentStore"), screenInput);
                _scModelUtils.setStringValueAtModelPath("Shipment.EnterpriseCode", _iasContextUtils.getFromContext("EnterpriseCode"), screenInput);
                setModelOptions = null;
                setModelOptions = {};
                _scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptions);
                _scBaseUtils.setAttributeValue("allowEmpty", true, setModelOptions);
                _scScreenUtils.setModel(
                    this, "screenInput", screenInput, setModelOptions);
            }
        },
        okCalled: function () {
            console.log('ok here we go');
            var editorInstance = _scEditorUtils.getCurrentEditor(this);
            this.SST_search();
            console.log('editorInstance', editorInstance);
            _scEditorUtils.refresh(editorInstance);

        }
      
        ,
        refreshShipmentSearch: function (
            event, bEvent, ctrl, args) {
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(
                this);
            var errorMessage = null;
            errorMessage = _scModelUtils.getStringValueFromPath("Shipment.ErrorMessage", screenInput);
            if (!(
                _scBaseUtils.isVoid(
                    errorMessage))) {
                _iasBaseTemplateUtils.showMessage(
                    this, _scScreenUtils.getString(
                        this, errorMessage), "error", null);
                var emptyModel = null;
                var resultScreen = null;
                emptyModel = {};
                emptyModel = _scModelUtils.createModelObjectFromKey("Page", emptyModel);
                _scModelUtils.setStringValueAtModelPath("Page.Output.Shipments.TotalNumberOfRecords", "0", emptyModel);
                resultScreen = _scScreenUtils.getChildScreen(
                    this, "extnShipmentSearchResult");
                _scScreenUtils.setModel(
                    resultScreen, "getList_output", emptyModel, null);
                _scRepeatingPanelUtils.hideDesktopPaginationBar(
                    resultScreen, "repeatingPanel", false);
            } else {
                var refreshResults = null;
                refreshResults = _scModelUtils.getStringValueFromPath("Shipment.RefreshSearchResult", screenInput);
                if (
                    _scBaseUtils.equals(
                        refreshResults, "true")) {
                    this.SST_search();
                }
            }
        },
        initializeScreenAfterLoad: function (
            event, bEvent, ctrl, args) {
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(
                this);
            var errorMessage = null;
            errorMessage = _scModelUtils.getStringValueFromPath("Shipment.ErrorMessage", screenInput);
            if (!(
                _scBaseUtils.isVoid(
                    errorMessage))) {
                _iasBaseTemplateUtils.showMessage(
                    this, _scScreenUtils.getString(
                        this, errorMessage), "error", null);
            }
            _scScreenUtils.setInitialInputData(
                this, { Shipment: {} });
        },
       
    });
});