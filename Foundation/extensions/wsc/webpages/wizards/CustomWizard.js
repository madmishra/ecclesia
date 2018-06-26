scDefine(["dojo/text!./templates/CustomWizard.html","scbase/loader!dojo/dom", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat",  "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!sc/plat/dojo/widgets/Wizard","scbase/loader!ias/widgets/IASBaseWizard",], function(
templateText,_dojodom, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _idxContentPane, _scplat,  _scBaseUtils, _scControllerUtils, _scEditorUtils, _scEventUtils, _scModelUtils, _scResourcePermissionUtils, _scScreenUtils, _scWidgetUtils, _scWizardUtils, _iasBaseWizard, _scWizard) {
    return _dojodeclare("extn.wizards.CustomWizard", [_scWizard], {
        templateString: templateText,
        setWizardTitle: function(
            event, bEvent, ctrl, args) {
                var titleKey = null;
                var titleDesc = null;
                // titleKey = _scBaseUtils.getAttributeValue("title", false, args);
                console.log(args);
                // this.title = titleKey;
            },
        uId: "customWizard",	
        packageName: "extn.wizards",
        className: "CustomWizard",
        showRelatedTaskInWizard: true,
        title: "TITLE_CustomWizard",
        namespaces: {
            targetBindingNamespaces: [],
            sourceBindingNamespaces: [{
                value: 'getEnterpriseDetails'
            }]
        },
		        events: [{
            name: 'onSaveSuccess'
        }],
        subscribers: {
            local: [{
                eventId: 'nextBttn2_onClick',
                sequence: '5',
                handler: {
                    methodName: "handleNext"
                }
            }, {
                eventId: 'prevBttn2_onClick',
                sequence: '5',
                handler: {
                    methodName: "handlePrevious"
                }
            }, {
                eventId: 'closeBttn2_onClick',
                sequence: '5',
                handler: {
                    methodName: "handleClose"
                }
            }, {
                eventId: 'nextBttn_onClick',
                sequence: '5',
                handler: {
                    methodName: "handleNext"
                }
            }, {
                eventId: 'prevBttn_onClick',
                sequence: '5',
                handler: {
                    methodName: "handlePrevious"
                }
            }, {
                eventId: 'closeBttn_onClick',
                sequence: '5',
                handler: {
                    methodName: "handleClose"
                }
            }
            ],
        },
        handleMashupOutput: function(
        mashupRefId, modelOutput, modelInput, mashupContext) {
           
        },

        handleClose: function(
        uiEvent, businessEvent, control, args) {
           
        },
        handleNext: function(
        uiEvent, businessEvent, control, args) {
            this.sendEventForSavePage("NEXT");
        },
        handleWizardCloseConfirmation: function(
        res) {

        },
        handlePrevious: function(
        uiEvent, businessEvent, control, args) {

        },
        onSaveSuccess: function(
        uiEvent, businessEvent, control, args) {
           
        },
        afterPrevious: function(
        uiEvent, businessEvent, control, args) {

        },
        afterSaveSuccessOnConfirm: function(
        uiEvent, businessEvent, control, args) {
            _scWizardUtils.confirmWizard(
            this);
        },
        callMeMaybe: function(){
            var shipmentModel = {}
            shipmentModel = _scBaseUtils.getValueFromPath("Title", args);
            console.log(shipmentModel);
        }
		
    });
});
