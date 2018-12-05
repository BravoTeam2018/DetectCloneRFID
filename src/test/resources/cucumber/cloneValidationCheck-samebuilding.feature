Feature: The validation system shall perform checks for possible clone cards within the same building
  Scenario: Card used at location "Stanford Department of Economics, 3rd Floor, West Wing, CA, USA" where previous event found (10 seconds before) at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". ImPossible to travel within time frame
    Given card "3307775e-15ac-415f-a99c-e978856c8ec0" used at panel "5ae6dbcd-9166-4d80-99d9-069e69bead15"
    And previous event found 10 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
    When check performed
    Then responds with validEvent "false"
    And responds within less than 1 seconds
  Scenario: Card used at location "Stanford Department of Economics, 3rd Floor, West Wing, CA, USA" where previous event found (20 seconds before) at location "Stanford Department of Economics, 1st Floor, West Wing, CA, USA". Possible to travel within time frame
    Given card "3307775e-15ac-415f-a99c-e978856c8ec0" used at panel "5ae6dbcd-9166-4d80-99d9-069e69bead15"
    And previous event found 20 seconds before at panel "5e11d5ee-7715-4080-bfe6-25c66d8ce821"
    When check performed
    Then responds with validEvent "true"
    And responds within less than 1 seconds
