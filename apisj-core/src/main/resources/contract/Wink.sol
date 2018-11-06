pragma solidity ^0.4.18;

contract WinkTest {
    /**
     * @dev 컨트렉트에서 수수료를 부담하도록 하기 위한 이벤트
     */
    event Wink (address beneficiary, address winker);
    event Text (address beneficiary, address winker);

    uint256 foo;

    constructor ()
    public
    {}

    function test()
    public
    {
        foo += 1;

        emit Wink(msg.sender, address(this));
    }

    function testNoWink()
    public
    {
        foo += 1;

        emit Text(msg.sender, address(this));
    }
}