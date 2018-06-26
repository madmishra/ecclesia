


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/mobile/home/search/SearchExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnSearchExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.mobile.home.search.SearchExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.mobile.home.search.SearchExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getShipmentList'
,
		 mashupId : 			'mobileHomeSearch_getShipmentList'
,
		 extnType : 			''

	}
,
	 		{
		 mashupRefId : 			'getShipmentList'
,
		 mashupId : 			'extn_mobileHomeSearch_getShipmentList'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_mobile_getShipmentList_ref'
,
		 mashupId : 			'extn_mobile_getShipmentList'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

