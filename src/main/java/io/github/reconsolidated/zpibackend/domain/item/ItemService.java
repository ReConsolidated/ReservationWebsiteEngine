package io.github.reconsolidated.zpibackend.domain.item;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.availability.Availability;
import io.github.reconsolidated.zpibackend.domain.comment.CommentService;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.item.response.UpdateItemFailure;
import io.github.reconsolidated.zpibackend.domain.item.response.UpdateItemResponse;
import io.github.reconsolidated.zpibackend.domain.item.response.UpdateItemSuccess;
import io.github.reconsolidated.zpibackend.domain.parameter.Parameter;
import io.github.reconsolidated.zpibackend.domain.parameter.ParameterService;
import io.github.reconsolidated.zpibackend.domain.reservation.Reservation;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationService;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationStatus;
import io.github.reconsolidated.zpibackend.domain.reservation.dtos.ReservationDto;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import io.github.reconsolidated.zpibackend.domain.storeConfig.StoreConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final StoreService storeService;
    @Lazy
    @Autowired
    private CommentService commentService;
    @Lazy
    @Autowired
    private ReservationService reservationService;
    private final ItemMapper itemMapper;
    private final ParameterService parameterService;

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow();
    }

    public Item getItemFromStore(Long itemId, String storeName) {
        return itemRepository.findByStoreStoreNameAndItemId(storeName, itemId)
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no item with id: %d in store: %s", itemId, storeName)));
    }


    public ItemDto getItemDto(Long itemId) {
        return itemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow());
    }

    public List<ItemDto> getItems(AppUser currentUser, String storeName) {

        Store store = storeService.getStore(storeName);
        if (!Objects.equals(currentUser.getId(), store.getOwnerAppUserId())) {
            throw new IllegalArgumentException("Only owner of a store can see all items!");
        }
        return itemRepository.findAllByStore_Id(store.getId()).stream().map(itemMapper::toItemDto).toList();
    }

    public List<Item> getStoreItems(String storeName) {
        Store store = storeService.getStore(storeName);
        return itemRepository.findAllByStore_Id(store.getId());
    }

    public List<ItemDto> getFilteredItems(String storeName) {

        Store store = storeService.getStore(storeName);
        List<Item> ite = itemRepository.findAllByStore_Id(store.getId());
        return ite
                .stream()
                .filter(item -> item.getActive() && !item.isFixedPast())
                .map(itemMapper::toItemDto)
                .toList();
    }

    public Item createItem(AppUser currentUser, String storeName, ItemDto itemDto) {
        Store store = storeService.getStore(storeName);
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        itemDto.getCustomAttributeList().forEach(parameterDto -> parameterDto.setId(null));
        return itemRepository.save(new Item(store, itemDto));
    }

    public UpdateItemResponse updateItem(AppUser currentUser, Long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        if (store.getStoreConfig().getCore().getFlexibility()) {

            List<Availability> deletedAvailabilities = item.getInitialSchedule()
                    .getAvailabilities()
                    .stream()
                    .filter(slot -> slot.getStartDateTime().isAfter(LocalDateTime.now()) &&
                            !itemDto.getSchedule().getScheduledRanges().contains(slot))
                    .toList();

            List<Reservation> itemReservations = reservationService
                    .getItemReservations(item.getItemId())
                    .stream()
                    .filter(reservation -> reservation.getStartDateTime().isAfter(LocalDateTime.now()))
                    .toList();
            List<Reservation> toProcess = new ArrayList<>();
            List<Reservation> causeError = new ArrayList<>();
            for (Reservation reservation : itemReservations) {
                if (deletedAvailabilities
                        .stream()
                        .anyMatch(availability ->
                                availability.overlap(new Availability(reservation.getScheduleSlot())))) {
                    causeError.add(reservation);
                } else {
                    toProcess.add(reservation);
                }
            }
            if (!causeError.isEmpty()) {
                return new UpdateItemFailure(causeError.stream().map(ReservationDto::new).toList());
            } else {
                item = new Item(store, itemDto);
                item.setItemId(itemId);
                item.setAvailableSchedule(itemDto.getSchedule().getScheduledRanges());
                for (Reservation reservation : toProcess) {
                    item.getSchedule().processReservation(store.getStoreConfig().getCore(), reservation);
                }
                item = itemRepository.save(item);
                return new UpdateItemSuccess(itemMapper.toItemDto(item));
            }
        }
        if (itemDto.getSubItems() != null) {
            itemDto.setAmount(null);
        }
        item = new Item(store, itemDto);
        item.setItemId(itemId);
        itemRepository.save(item);
        return new UpdateItemSuccess(itemMapper.toItemDto(item));
    }

    public ItemDto activateItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setActive(true);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto deactivateItem(AppUser currentUser, Long itemId) {
        Item item = getItem(itemId);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        item.setActive(false);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public boolean deleteItem(AppUser currentUser, String storeName, Long itemId) {
        Item item = getItemFromStore(itemId, storeName);
        Store store = item.getStore();
        if (!store.getStoreConfig().getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this store");
        }
        List<Reservation> itemReservations = reservationService.getItemReservations(item.getItemId());
        if (!itemReservations.isEmpty()) {
            for (Reservation reservation : itemReservations) {
                if (reservation.getStatus() == ReservationStatus.ACTIVE) {
                    return false;
                }
            }
        }
        itemReservations.forEach(reservation ->
                reservationService.deletePastReservation(currentUser, reservation.getReservationId()));
        commentService.getComments(item.getItemId())
                .forEach(commentDto -> commentService.deleteComment(currentUser, item.getItemId(), commentDto.getId()));
        itemRepository.deleteById(item.getItemId());
        return true;
    }

    @Transactional
    public void updateCustomAttribute(AppUser currentUser, StoreConfig newStoreConfig) {
        if (newStoreConfig.getStoreConfigId() == null) {
            throw new IllegalArgumentException("Updated Store Config Id cannot be null.");
        }
        Store store = storeService.getStore(newStoreConfig.getOwner().getStoreName().replaceAll("[ /]", "_"));
        StoreConfig currentStoreConfig = store.getStoreConfig();
        if (currentStoreConfig.getOwner() == null || currentStoreConfig.getOwner().getAppUserId() == null
                || !currentStoreConfig.getOwner().getAppUserId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You are not the owner of this Store. You cannot edit it.");
        }
        // Core Config cannot be edited
        if (!currentStoreConfig.getCore().equals(newStoreConfig.getCore())) {
            throw new IllegalArgumentException("Core Config cannot be edited");
        }
        List<Integer> settingsToDelete = new ArrayList<>();
        for (int i = 0; i < currentStoreConfig.getCustomAttributesSpec().size(); i++) {
            int finalI = i;
            if (newStoreConfig.getCustomAttributesSpec()
                    .stream()
                    .noneMatch(parameterSettings ->
                            Objects.equals(
                                    parameterSettings.getId(),
                                    currentStoreConfig.getCustomAttributesSpec().get(finalI).getId()))) {
                settingsToDelete.add(i);
            }
        }
        for (Integer index : settingsToDelete) {
            for (Item item : getStoreItems(store.getStoreName())) {
                List<Parameter> parameters = item.getCustomAttributeList();
                parameterService.deleteParameter(parameters.get(index).getId());
                parameters.remove(index.intValue());
                item.setCustomAttributeList(parameters);
            }
        }
        for (int i = currentStoreConfig.getCustomAttributesSpec().size() - settingsToDelete.size();
            i < newStoreConfig.getCustomAttributesSpec().size(); i++) {
            for (Item item : getStoreItems(store.getStoreName())) {
                List<Parameter> parameters = item.getCustomAttributeList();
                parameters.add(
                        i,
                        new Parameter(
                                null,
                                item,
                                newStoreConfig.getCustomAttributesSpec().get(i).getName(),
                                ""));
                item.setCustomAttributeList(parameters);
            }
        }
        storeService.saveStore(store);
    }
}
