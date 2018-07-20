scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/mobile/custom/sort/SortScan"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnSearchResult) {
    return _dojodeclare("extn.mobile.custom.sort.SortScanBehaviorController", [_scServerDataController], {
        screenId: 'extn.mobile.custom.sort.SortScan',
		 mashupRefs: [{
            mashupId: 'backroomPickUp_registerBarcodeForBackroomPick',
            mashupRefId: 'translateBarCode'
	},
	{
            mashupId: 'extn_getShipmentForSort',
            mashupRefId: 'extn_getDetails'
	}]
        
    });
});