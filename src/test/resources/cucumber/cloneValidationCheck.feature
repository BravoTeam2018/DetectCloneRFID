Feature: The validation system shall perform checks for possible clone cards
  Scenario: Card used at location "CIT Library West Wing Entry Doors, Cork, Ireland" where previous event found at same location. Possible to travel within time frame
    Given card "5507775e-15ac-415f-a99c-e978856c8ec0" used at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
    And previous event found 120 seconds before at panel "580ddc98-0db9-473d-a721-348f353f1d2b"
    When check performed
    Then responds with validEvent "true"
    And responds within less than 1 seconds

