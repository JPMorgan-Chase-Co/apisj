/*
 * Copyright (c) [2018] [ <APIS> ]
 * This file is part of the apisJ library.
 *
 * The apisJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The apisJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the apisJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apis.validator;

import org.apis.core.BlockHeader;
import org.apis.util.BIUtil;

import java.math.BigInteger;

/**
 * Checks block's reward point against calculated value
 * 블록에 기록된 RP 값이 계산된 RP 값과 일치하는지 검증한다.
 *
 * @author Daniel
 * @since 04.22.2018
 */
public class RewardPointRule extends DependentBlockHeaderRule {

    @Override
    public boolean validate(BlockHeader header, BlockHeader parent) {

        errors.clear();

        BigInteger rewardPoint = header.getRewardPoint();
        //BigInteger calcRewardPoint = header.calcRewardPointByBlockInfo();
        BigInteger calcRewardPoint = BigInteger.ZERO;   // 사용하지 않으므로 0 으로 변경..

        if(BIUtil.isNotEqual(rewardPoint, calcRewardPoint)) {
            errors.add(String.format("#%d: rewardPoint(%s) != calcRewardPoint(%s)", header.getNumber(), rewardPoint.toString(10), calcRewardPoint.toString(10)));
            return false;
        }

        return true;
    }
}
