package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemService;
import io.github.reconsolidated.zpibackend.features.item.SubItem;
import io.github.reconsolidated.zpibackend.features.reservation.dtos.ReservationDto;
import io.github.reconsolidated.zpibackend.features.reservation.dtos.UserReservationDto;
import io.github.reconsolidated.zpibackend.features.reservation.request.CheckAvailabilityRequest;
import io.github.reconsolidated.zpibackend.features.reservation.response.CheckAvailabilityResponse;
import io.github.reconsolidated.zpibackend.features.reservation.response.CheckAvailabilityResponseFailure;
import io.github.reconsolidated.zpibackend.features.reservation.response.CheckAvailabilityResponseSuccess;
import io.github.reconsolidated.zpibackend.features.reservation.response.CheckAvailabilityResponseSuggestion;
import io.github.reconsolidated.zpibackend.features.store.StoreService;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private StoreService storeService;
    private ItemService itemService;
    private AppUserService appUserService;

    public ReservationDto reserveItem(AppUser appUser, ReservationDto reservationDto) {
        Item item = itemService.getItem(reservationDto.getItemId());
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        Optional<AppUser> appUserOptional = appUserService.getUserByEmail(reservationDto.getUserEmail());
        if(appUserOptional.isPresent()) {
            appUser = appUserOptional.get();
        }

        List<String> personalData = new ArrayList<>();
        for (String data : item.getStore().getStoreConfig().getAuthConfig().getRequiredPersonalData()) {
            personalData.add(reservationDto.getPersonalData().getOrDefault(data, ""));
        }

        if (core.getFlexibility()) {
            //reservations with schedule
            Schedule schedule = item.getSchedule();
            ScheduleSlot requestSlot = new ScheduleSlot(reservationDto.getStartDateTime(), reservationDto.getEndDateTime(),
                    reservationDto.getAmount());
            if (!schedule.verify(core.getGranularity(), requestSlot)) {
                throw new IllegalArgumentException("Right slot is not available. Reservation is not possible!");
            }

            Reservation reservation = Reservation.builder()
                    .user(appUser)
                    .personalData(personalData)
                    .item(item)
                    .startDateTime(reservationDto.getStartDateTime())
                    .endDateTime(reservationDto.getEndDateTime())
                    .amount(reservationDto.getAmount())
                    .subItemIdList(new ArrayList<>())
                    .confirmed(!item.getStore().getStoreConfig().getAuthConfig().getConfirmationRequire())
                    .build();
            reservation.setStatus(LocalDateTime.now());
            schedule.processReservation(reservation);

            return new ReservationDto(reservationRepository.save(reservation), reservationDto.getPersonalData());
        } else {
            if (core.getPeriodicity() || core.getSpecificReservation()) {
                //reservations with sub items
                ArrayList<SubItem> toReserve = new ArrayList<>();
                for (SubItem subItem : item.getSubItems()) {
                    for (Long subItemId: reservationDto.getSubItemIds()) {
                        if (subItem.getSubItemId().equals(subItemId)) {
                            toReserve.add(subItem);
                        }
                    }
                }
                for (SubItem subItem : toReserve) {
                    if (subItem.getAmount() <= reservationDto.getAmount()) {
                        subItem.setAmount(subItem.getAmount() - reservationDto.getAmount());
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                Reservation reservation = Reservation.builder()
                        .user(appUser)
                        .personalData(personalData)
                        .item(item)
                        .startDateTime(toReserve.get(0).getSlot().getStartDateTime())
                        .endDateTime(toReserve.get(0).getSlot().getEndDateTime())
                        .subItemIdList(reservationDto.getSubItemIds())
                        .amount(reservationDto.getAmount())
                        .confirmed(!item.getStore().getStoreConfig().getAuthConfig().getConfirmationRequire())
                        .build();
                reservation.setStatus(LocalDateTime.now());
                return new ReservationDto(reservationRepository.save(reservation), reservationDto.getPersonalData());
            } else {
                //simple reservations IDK if it will be useful
                if (item.getAmount() < reservationDto.getAmount()) {
                    throw new IllegalArgumentException();
                }
                item.setAmount(item.getAmount() - reservationDto.getAmount());
                Reservation reservation = Reservation.builder()
                        .user(appUser)
                        .personalData(personalData)
                        .item(item)
                        .startDateTime(LocalDateTime.now())
                        .endDateTime(LocalDateTime.now())
                        .subItemIdList(new LinkedList<>())
                        .amount(reservationDto.getAmount())
                        .confirmed(false)
                        .build();
                reservation.setStatus(LocalDateTime.now());
                return new ReservationDto(reservationRepository.save(reservation), reservationDto.getPersonalData());
            }
        }
    }

    public List<CheckAvailabilityResponse> checkAvailability(CheckAvailabilityRequest request) {

        Item item = itemService.getItem(request.getItemId());
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        ScheduleSlot requestSlot = new ScheduleSlot(request.getStartDate(), request.getEndDate(), request.getAmount());
        if (schedule.verify(core.getGranularity(), requestSlot)) {
            return Collections.singletonList(CheckAvailabilityResponseSuccess.builder()
                    .itemId(request.getItemId())
                    .amount(request.getAmount())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .build());
        } else {
            List<ScheduleSlot> suggestions = schedule.suggest(requestSlot);
            if (suggestions.isEmpty()) {
                return Collections.singletonList(CheckAvailabilityResponseFailure.builder()
                        .itemId(item.getItemId())
                        .amount(request.getAmount())
                        .build());
            } else {
                List<CheckAvailabilityResponse> result = new ArrayList<>();
                for (int i = 0; i < (Math.min(suggestions.size(), 3)); i++) {
                    result.add(new CheckAvailabilityResponseSuggestion(
                            i,
                            item.getItemId(),
                            suggestions.get(i),
                            item.getSchedule().getAvailabilitiesForSubItems(suggestions.get(i).getAvailableItemsIndexes())));
                }
                return result;
            }
        }
    }

    public List<Reservation> getUserReservations(Long currentUserId, String storeName) {
        return reservationRepository.findByUser_IdAndItemStoreStoreName(currentUserId, storeName);
    }

    public List<UserReservationDto> getUserReservationsDto(Long currentUserId, String storeName) {

        return getUserReservations(currentUserId, storeName)
                .stream()
                .map(reservation -> new UserReservationDto(reservation,
                                itemService.getItem(reservation.getItem().getItemId())
                                        .getSubItemsListDto()
                                        .subItems()
                                        .stream()
                                        .filter(subItemDto -> reservation.getSubItemIdList().contains(subItemDto.getId()))
                                        .toList()
                    )
                ).toList();
    }

    public List<Reservation> getStoreReservations(AppUser currentUser, String storeName) {
        if (!currentUser.getId().equals(storeService.getStore(storeName).getOwnerAppUserId())) {
            throw new IllegalArgumentException("Only owner can get all reservations");
        }
        return reservationRepository.findByItemStoreStoreName(storeName);
    }

    public void deleteReservation(AppUser appUser, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        Item item = reservation.getItem();
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        if (core.getFlexibility()) {
            schedule.processReservationRemoval(reservation);
        } else {
            if (core.getPeriodicity() || core.getSpecificReservation()) {
                ArrayList<SubItem> toReserve = new ArrayList<>();
                for (SubItem subItem : item.getSubItems()) {
                    for (Long subItemId: reservation.getSubItemIdList()) {
                        if (subItem.getSubItemId().equals(subItemId)) {
                            toReserve.add(subItem);
                        }
                    }
                }
                for (SubItem subItem : toReserve) {
                    subItem.setAmount(subItem.getAmount() + reservation.getAmount());
                }
            } else {
                item.setAmount(item.getAmount() + reservation.getAmount());
            }
        }

        reservationRepository.delete(reservation);
    }
}
