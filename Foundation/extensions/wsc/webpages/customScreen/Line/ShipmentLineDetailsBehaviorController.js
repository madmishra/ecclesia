scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController", "scbase/loader!extn/customScreen/Line/ShipmentLineDetails"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController, _extnShipmentLineDetails) {
    return _dojodeclare("extn.customScreen.Line.ShipmentLineDetailsBehaviorController", [_scServerDataController], {
        screenId: 'extn.customScreen.Line.ShipmentLineDetails',
       
			 mashupRefs : 	[
	 		{
		
		 mashupId : 			'extn_changeShipmentStatus'
,
		 mashupRefId : 			'extn_printStatus'

	}

	]
    });
});