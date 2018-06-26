scDefine(["dojo/text!./templates/BackroomPickupWizard.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!ias/widgets/IASBaseWizard", "scbase/loader!idx/layout/ContentPane", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/Image"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _iasIASBaseWizard, _idxContentPane, _scplat, _scButtonDataBinder, _scCurrencyDataBinder, _scImageDataBinder, _scBaseUtils, _scDataLabel, _scImage) {
    return _dojodeclare("extn.wizards.BackroomPickupWizardUI", [_iasIASBaseWizard], {
        templateString: templateText,
        uId: "backroomPickupWizard",
        packageName: "extn.wizards",
        className: "BackroomPickupWizard",
        title: "Title_GiftWrap",
        screen_description: "Gift Wrap Wizard",
        wizardCompletionEditor: 'wsc.desktop.editors.ShipmentEditor',
        wizardCompletionScreen: 'wsc.components.shipment.summary.ShipmentSummaryWizard',
        showRelatedTaskInWizard: false,
        closeTab: false,
        hideNavigationPanelForSinglePage: false,
        viewOnlyWizard: false,
        flowImage: 'wsc/resources/css/icons/images/gift.png',
        isDirtyCheckRequired: true,
        namespaces: {
            targetBindingNamespaces: [],
            sourceBindingNamespaces: [{
                value: 'Shipment',
                description: "Shipment Details model."
            }, {
                value: 'clockImageBindingValues',
                description: "Values to be binded to the time threshold clock image"
            }]
        },
        staticBindings: [],
        hotKeys: [{
            id: "closebtn",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "closeBttn2",
            invocationContext: "Editor",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }, {
            id: "closebtn",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "closeBttn",
            invocationContext: "Editor",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
        events: [{
            name: 'onSaveSuccess'
        }, {
            name: 'showNextPage'
        }, {
            name: 'saveSuccessOnConfirm'
        }, {
            name: 'setScreenTitle'
        }, {
            name: 'handleTabClose'
        }, {
            name: 'hidePreviousButtons'
        }, {
            name: 'showPreviousButtons'
        }, {
            name: 'enableNextButton'
        }, {
            name: 'disableNextButton'
        }, {
            name: 'handleMobileBackButton'
        }, {
            name: 'setWizardTitle'
        }, {
            name: 'setWizardDescription'
        }, {
            name: 'combinedAPICallOnNext'
        }],
        subscribers: {
            local: [{
                eventId: 'nextBttn2_onClick',
                sequence: '25',
                description: 'Handler for next button onClick event',
                handler: {
                    methodName: "handleNext",
                    description: "Handler for next event"
                }
            }, {
                eventId: 'nextBttn_onClick',
                sequence: '25',
                description: 'Handler for next button onClick event',
                handler: {
                    methodName: "handleNext",
                    description: "Handler for next event"
                }
            }, {
                eventId: 'showNextPage',
                sequence: '25',
                description: 'Handler for show next page event',
                handler: {
                    methodName: "showNextPage",
                    description: "Handler for show next page event"
                }
            }, {
                eventId: 'prevBttn2_onClick',
                sequence: '25',
                description: 'Handler for previous button onClick event',
                handler: {
                    methodName: "handlePrevious",
                    description: "Handler for previous button onClick event"
                }
            }, {
                eventId: 'prevBttn_onClick',
                sequence: '25',
                description: 'Handler for previous button onClick event',
                handler: {
                    methodName: "handlePrevious",
                    description: "Handler for previous button onClick event"
                }
            }, {
                eventId: 'handleMobileBackButton',
                sequence: '25',
                description: 'Handler for mobile back button onClick event',
                handler: {
                    methodName: "handlePrevious",
                    description: "Handler for previous button onClick event"
                }
            }, {
                eventId: 'gwbutton_onClick',
                sequence: '25',
                description: 'Handler for confirm button onClick event',
                handler: {
                    methodName: "handleConfirm",
                    description: "Handler for confirm button onClick event"
                }
            }, {
                eventId: 'gwbutton2_onClick',
                sequence: '25',
                description: 'Handler for confirm button onClick event',
                handler: {
                    methodName: "handleConfirm",
                    description: "Handler for confirm button onClick event"
                }
            }, {
                eventId: 'closeBttn2_onClick',
                sequence: '25',
                description: 'Handler for close button onClick event',
                handler: {
                    methodName: "handleClose",
                    description: "Handler for close button onClick event"
                }
            }, {
                eventId: 'closeBttn_onClick',
                sequence: '25',
                description: 'Handler for close button onClick event',
                handler: {
                    methodName: "handleClose",
                    description: "Handler for close button onClick event"
                }
            }, {
                eventId: 'afterconfirm',
                sequence: '25',
                description: 'Handler for after confirm event',
                handler: {
                    methodName: "afterWizardConfirm",
                    description: "Handler for after confirm event"
                }
            }, {
                eventId: 'screenchanged',
                sequence: '25',
                description: 'Handler for screen changed event',
                handler: {
                    methodName: "handleScreenChanged",
                    description: "Handler for screen changed event"
                }
            }, {
                eventId: 'onSaveSuccess',
                sequence: '25',
                description: 'Handler for on save success event',
                handler: {
                    methodName: "onSaveSuccess",
                    description: "Handler for on save success event"
                }
            }, {
                eventId: 'setScreenTitle',
                sequence: '25',
                description: 'Handler for setScreenTitle',
                handler: {
                    methodName: "setScreenTitle",
                    description: "Handler for setScreenTitle"
                }
            }, {
                eventId: 'saveSuccessOnConfirm',
                sequence: '25',
                description: 'Handler for save Success on confirm event',
                handler: {
                    methodName: "afterSaveSuccessOnConfirm",
                    description: "Handler for save Success on confirm event"
                }
            }, {
                eventId: 'handleTabClose',
                sequence: '25',
                description: 'Handler for savetab close event',
                handler: {
                    methodName: "handleTabClose",
                    description: "Handler for savetab close event"
                }
            }, {
                eventId: 'start',
                sequence: '26',
                description: 'Handler for wizard start event',
                handler: {
                    methodName: "onStartWizard",
                    description: "Handler for wizard start event"
                }
            }, {
                eventId: 'previous',
                sequence: '25',
                description: 'Handler for wizard previous event',
                handler: {
                    methodName: "afterPrevious",
                    description: "Handler for wizard previous event"
                }
            }, {
                eventId: 'hidePreviousButtons',
                sequence: '25',
                description: 'Handler for hide previous button event',
                handler: {
                    methodName: "handleHidePrevious",
                    description: "Handler for hide previous button event"
                }
            }, {
                eventId: 'showPreviousButtons',
                sequence: '25',
                description: 'Handler for show previous button event',
                handler: {
                    methodName: "handleShowPrevious",
                    description: "Handler for show previous button event"
                }
            }, {
                eventId: 'enableNextButton',
                sequence: '25',
                description: 'Handler for enable next button event',
                handler: {
                    methodName: "enableNextButton",
                    description: "Handler for enable next button event"
                }
            }, {
                eventId: 'disableNextButton',
                sequence: '25',
                description: 'Handler for disable next button event',
                handler: {
                    methodName: "disableNextButton",
                    description: "Handler for disable next button event"
                }
            }, {
                eventId: 'setWizardTitle',
                sequence: '25',
                handler: {
                    methodName: "setWizardTitle"
                }
            }, {
                eventId: 'setWizardDescription',
                sequence: '25',
                handler: {
                    methodName: "setWizardDescription"
                }
            }, {
                eventId: 'combinedAPICallOnNext',
                sequence: '60',
                description: '',
                handler: {
                    methodName: "combinedAPICallOnNext",
                    description: ""
                }
            }, {
                eventId: 'screenchanged',
                sequence: '32',
                description: '',
                handler: {
                    methodName: "changeConfirmButtonLiteralIfLastPage"
                }
            }],
            global: []
        }
    });
});