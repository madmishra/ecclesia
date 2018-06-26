scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/IdentifierController", "scbase/loader!extn/mobile/picking/ShipmentPickDetails"], function(
_dojodeclare, _dojokernel, _dojotext, _scIdentifierController, _extnShipmentPickDetails) {
    return _dojodeclare("extn.mobile.picking.ShipmentPickDetailsInitController", [_scIdentifierController], {
        screenId: 'extn.mobile.picking.ShipmentPickDetails',
        className: 'ShipmentPickDetails',
        identifierTemplatesRootPath: 'extn.mobile.picking.identifiers',
        baseTemplateFolder: 'extn.mobile.picking.templates'
    });
});