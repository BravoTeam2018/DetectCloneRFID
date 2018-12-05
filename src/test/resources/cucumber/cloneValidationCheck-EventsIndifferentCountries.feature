Feature: The validation system shall perform checks for possible clone cards w, where current and previous events were in different countries
  Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Possible to travel within time frame
    Given card "4407775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
    And previous event found 120 seconds before at panel "d50f91a5-6f2b-4a70-ab6c-e0fec58c866e"
    When check performed
    Then responds with validEvent "false"
    And responds within less than 1 seconds
  Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found (48hours before) at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Possible to travel within time frame
    Given card "4407775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
    And previous event found 172800 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
    When check performed
    Then responds with validEvent "true"
    And responds within less than 1 seconds
  Scenario: Card used at location "CIT Library North Ground Exit, Cork, Ireland" where previous event found (60 seconds before) at a location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Impossible to travel within time frame
    Given card "6607775e-15ac-415f-a99c-e978856c8ec0" used at panel "7907775e-15ac-415f-a99c-e978856c8ec0"
    And previous event found 60 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
    When check performed
    Then responds with validEvent "false"
    And responds within less than 1 seconds