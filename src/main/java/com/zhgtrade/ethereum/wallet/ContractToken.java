package com.zhgtrade.ethereum.wallet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link org.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version 2.3.0.
 */
public final class ContractToken extends Contract {
    private static final String BINARY = "606060405260408051908101604052600e81527f5a68616f4775436f696e20302e310000000000000000000000000000000000006020820152600090805161004b929160200190610126565b50341561005757600080fd5b5b600160a060020a033316600090815260056020526040908190206a1f6ed020b26b45e6000000908190556004558051908101604052600a81527f5a68616f4775436f696e00000000000000000000000000000000000000000000602082015260019080516100ca929160200190610126565b5060408051908101604052600381527f5a4743000000000000000000000000000000000000000000000000000000000060208201526002908051610112929160200190610126565b506003805460ff191660121790555b6101c6565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061016757805160ff1916838001178555610194565b82800160010185558215610194579182015b82811115610194578251825591602001919060010190610179565b5b506101a19291506101a5565b5090565b6101c391905b808211156101a157600081556001016101ab565b5090565b90565b610b74806101d56000396000f300606060405236156100c25763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100c7578063095ea7b31461015257806318160ddd1461018857806323b872dd146101ad578063313ce567146101e957806342966c68146102125780635a3b7e421461023c57806370a08231146102c757806379cc6790146102f857806395d89b411461032e578063a9059cbb146103b9578063cae9ca51146103dd578063dd62ed3e14610456575b600080fd5b34156100d257600080fd5b6100da61048d565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101175780820151818401525b6020016100fe565b50505050905090810190601f1680156101445780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561015d57600080fd5b610174600160a060020a036004351660243561052b565b604051901515815260200160405180910390f35b341561019357600080fd5b61019b61055c565b60405190815260200160405180910390f35b34156101b857600080fd5b610174600160a060020a0360043581169060243516604435610562565b604051901515815260200160405180910390f35b34156101f457600080fd5b6101fc610688565b60405160ff909116815260200160405180910390f35b341561021d57600080fd5b610174600435610691565b604051901515815260200160405180910390f35b341561024757600080fd5b6100da61071d565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101175780820151818401525b6020016100fe565b50505050905090810190601f1680156101445780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102d257600080fd5b61019b600160a060020a03600435166107bb565b60405190815260200160405180910390f35b341561030357600080fd5b610174600160a060020a03600435166024356107cd565b604051901515815260200160405180910390f35b341561033957600080fd5b6100da61088d565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156101175780820151818401525b6020016100fe565b50505050905090810190601f1680156101445780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156103c457600080fd5b6103db600160a060020a036004351660243561092b565b005b34156103e857600080fd5b61017460048035600160a060020a03169060248035919060649060443590810190830135806020601f820181900481020160405190810160405281815292919060208401838380828437509496506109f795505050505050565b604051901515815260200160405180910390f35b341561046157600080fd5b61019b600160a060020a0360043581169060243516610b2b565b60405190815260200160405180910390f35b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105235780601f106104f857610100808354040283529160200191610523565b820191906000526020600020905b81548152906001019060200180831161050657829003601f168201915b505050505081565b600160a060020a03338116600090815260066020908152604080832093861683529290522081905560015b92915050565b60045481565b6000600160a060020a038316151561057957600080fd5b600160a060020a0384166000908152600560205260409020548290101561059f57600080fd5b600160a060020a03831660009081526005602052604090205482810110156105c657600080fd5b600160a060020a03808516600090815260066020908152604080832033909416835292905220548211156105f957600080fd5b600160a060020a03808516600081815260056020908152604080832080548890039055878516808452818420805489019055848452600683528184203390961684529490915290819020805486900390557fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060015b9392505050565b60035460ff1681565b600160a060020a033316600090815260056020526040812054829010156106b757600080fd5b600160a060020a03331660008181526005602052604090819020805485900390556004805485900390557fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59084905190815260200160405180910390a25060015b919050565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105235780601f106104f857610100808354040283529160200191610523565b820191906000526020600020905b81548152906001019060200180831161050657829003601f168201915b505050505081565b60056020526000908152604090205481565b600160a060020a038216600090815260056020526040812054829010156107f357600080fd5b600160a060020a038084166000908152600660209081526040808320339094168352929052205482111561082657600080fd5b600160a060020a03831660008181526005602052604090819020805485900390556004805485900390557fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59084905190815260200160405180910390a25060015b92915050565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105235780601f106104f857610100808354040283529160200191610523565b820191906000526020600020905b81548152906001019060200180831161050657829003601f168201915b505050505081565b600160a060020a038216151561094057600080fd5b600160a060020a0333166000908152600560205260409020548190101561096657600080fd5b600160a060020a038216600090815260056020526040902054818101101561098d57600080fd5b600160a060020a033381166000818152600560205260408082208054869003905592851680825290839020805485019055917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9084905190815260200160405180910390a35b5050565b600083610a04818561052b565b15610b225780600160a060020a0316638f4ffcb1338630876040518563ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018085600160a060020a0316600160a060020a0316815260200184815260200183600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360005b83811015610abb5780820151818401525b602001610aa2565b50505050905090810190601f168015610ae85780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b1515610b0957600080fd5b6102c65a03f11515610b1a57600080fd5b505050600191505b5b509392505050565b6006602090815260009283526040808420909152908252902054815600a165627a7a72305820df83d7e17a1165a1ba8780f2ee10a3fc769daab81065efb36caf8932f246eaab0029";

    private ContractToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private ContractToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Burn", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Burn", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Future<Utf8String> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> approve(Address _spender, Uint256 _value) {
        Function function = new Function("approve", Arrays.<Type>asList(_spender, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> totalSupply() {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transferFrom(Address _from, Address _to, Uint256 _value) {
        Function function = new Function("transferFrom", Arrays.<Type>asList(_from, _to, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint8> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> burn(Uint256 _value) {
        Function function = new Function("burn", Arrays.<Type>asList(_value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Utf8String> standard() {
        Function function = new Function("standard", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Uint256> balanceOf(Address param0) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(param0), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> burnFrom(Address _from, Uint256 _value) {
        Function function = new Function("burnFrom", Arrays.<Type>asList(_from, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Utf8String> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transfer(Address _to, Uint256 _value) {
        Function function = new Function("transfer", Arrays.<Type>asList(_to, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> approveAndCall(Address _spender, Uint256 _value, DynamicBytes _extraData) {
        Function function = new Function("approveAndCall", Arrays.<Type>asList(_spender, _value, _extraData), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> allowance(Address param0, Address param1) {
        Function function = new Function("allowance", 
                Arrays.<Type>asList(param0, param1), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public static Future<ContractToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(ContractToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static Future<ContractToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(ContractToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static ContractToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ContractToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public Address from;

        public Address to;

        public Uint256 value;
    }

    public static class BurnEventResponse {
        public Address from;

        public Uint256 value;
    }
}
