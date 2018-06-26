scDefine(["dojo/text!./templates/SecondaryPickPerson.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/widgets/Screen", "scbase/loader!ias/widgets/IASBaseScreen", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils"], function (
    templateText, _dojodeclare, _dojokernel, _iasUIUtils, _scScreenUtils, _scScreen, _iasIASBaseScreen, _scWizardUtils, _scWidgetUtils, _scEventUtils) {
    return _dojodeclare("extn.customScreen.popup.SecondaryPickPersonUI", [_scScreen], {
        templateString: templateText,
        uId: "extnSecondaryPickPerson",
        packageName: "extn.customScreen.popup",
        className: "SecondaryPickPerson",
        title: "",
        namespaces: {
            targetBindingNamespaces: [],
            sourceBindingNamespaces: [{
                value: 'popupOutput',
                description: "Stores popup output set from setPopupOutput"
            }]
        },
        showRelatedTask: false,
        isDirtyCheckRequired: true,
        hotKeys: [{
            id: "Popup_btnCancel",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_btnCancel",
            invocationContext: "",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
        events: [],
        subscribers: {
            global: [],
            local: [{
                eventId: 'Popup_btnOkay_onClick',
                sequence: '25',
                description: '',
                listeningControlUId: 'Popup_btnCancel',
                handler: {
                    methodName: "onPopupClose",
                    description: ""
                }
            }]
        },

        onPopupClose: function (
            event, bEvent, ctrl, args) {

            //_iasUIUtils.openWizardInEditor("extn.customScreen.search.extnShipmentSearchWizard",shipmentModel, "extn.editors.MyOrderEditor1", this, null);
            batchModel = _scScreenUtils.getTargetModel(this, "secondaryPersonObject");
            _scEventUtils.fireEventGlobally(
                this, "updateSecondaryContact", null, {
                    SecondaryPerson: batchModel.Name
                });
            _scWidgetUtils.closePopup(
                this, "CLOSE", false);
        },

    });
});